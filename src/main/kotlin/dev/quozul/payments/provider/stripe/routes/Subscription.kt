package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.InvalidRequestException
import com.stripe.exception.StripeException
import com.stripe.model.Coupon
import com.stripe.model.PaymentIntent
import com.stripe.model.PromotionCode
import com.stripe.model.Subscription as StripeSubscription
import com.stripe.param.PaymentIntentRetrieveParams
import com.stripe.param.PromotionCodeListParams
import com.stripe.param.PromotionCodeRetrieveParams
import com.stripe.param.SubscriptionCreateParams
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.*
import dev.quozul.payments.provider.stripe.getOrCreateStripeCustomer
import dev.quozul.payments.provider.stripe.models.ApiPaymentIntentUpdate
import dev.quozul.payments.provider.stripe.models.ApiCart
import dev.quozul.payments.provider.stripe.models.ApiPromoCode
import dev.quozul.payments.provider.stripe.models.SubscriptionCreateResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureServerSubscriptionRoutes() {
	// Subscription intent
	post("") {
		// We have only one product, so no input is expected
		val body = try {
			call.receive<ApiCart>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val user = transaction {
			User.findById(UUID.fromString(id))
		}

		// Get the Stripe customer from the database
		if (user != null) {
			// Verify user doesn't already own a product
			val alreadyOwns = transaction {
				body.cart.forEach {
					val query = SubscriptionItems.innerJoin(Subscriptions)
						.slice(SubscriptionItems.product, Subscriptions.owner)
						.select {
							(SubscriptionItems.product eq UUID.fromString(it)) and (Subscriptions.owner eq user.id)
						}
						.withDistinct()

					if (SubscriptionItem.wrapRows(query).count() > 0) {
						return@transaction true
					}
				}

				false
			}

			if (alreadyOwns) {
				call.response.status(HttpStatusCode.BadRequest)
				call.respond(AuthenticationErrors.ALREADY_OWNS.toHashMap(true))
				return@post
			}

			val customer = getOrCreateStripeCustomer(user)

			val paymentSettings = SubscriptionCreateParams.PaymentSettings.builder()
				.setSaveDefaultPaymentMethod(SubscriptionCreateParams.PaymentSettings.SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
				.build()

			val priceIds = transaction {
				body.cart.mapNotNull {
					SubscriptionCreateParams.Item.builder().setPrice(
						Product.findById(UUID.fromString(it))?.stripeId
					).build()
				}
			}

			val promotionCode = body.promo?.let {
				val params = PromotionCodeListParams.builder()
					.setCode(body.promo)
					.addExpand("data.coupon")
					.setLimit(1)
					.setActive(true)
					.build()

				PromotionCode.list(params).data.firstOrNull()?.id
			}

			val subCreateParams = SubscriptionCreateParams.builder()
				.setCustomer(customer.id)
				.addAllItem(priceIds)
				.setPromotionCode(promotionCode)
				.setPaymentSettings(paymentSettings)
				.setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
				.addAllExpand(listOf("latest_invoice.payment_intent"))
				.build()

			val subscription = try {
				StripeSubscription.create(subCreateParams)
			} catch (e: InvalidRequestException) {
				when (e.param) {
					"promotion_code" -> {
						call.response.status(HttpStatusCode.BadRequest)
						call.respond(AuthenticationErrors.INVALID_PROMO_CODE.toHashMap(true))
					}
					else -> {
						e.printStackTrace()
						call.response.status(HttpStatusCode.InternalServerError)
						call.respond(AuthenticationErrors.STRIPE_ERROR.toHashMap(true))
					}
				}

				return@post
			} catch (e: StripeException) {
				e.printStackTrace()
				call.response.status(HttpStatusCode.InternalServerError)
				call.respond(AuthenticationErrors.STRIPE_ERROR.toHashMap(true))
				return@post
			}

			val response = SubscriptionCreateResponse(
				subscription.latestInvoiceObject.total,
				subscription.latestInvoiceObject.paymentIntentObject.clientSecret,
			)

			call.response.status(HttpStatusCode.Created)
			call.respond(response)
		}
	}

	put("") {
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val user = transaction {
			User.findById(UUID.fromString(id))
		}

		if (user == null) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val body = try {
			call.receive<ApiPaymentIntentUpdate>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val paymentIntent = try {
			val params = PaymentIntentRetrieveParams.builder()
				.addAllExpand(listOf("invoice"))
				.build()
			PaymentIntent.retrieve(body.paymentIntentId, params, null)
		} catch (e: StripeException) {
			e.printStackTrace()
			call.response.status(HttpStatusCode.InternalServerError)
			return@put
		}

		val subscription = getOrCreateSubscriptionFromInvoice(paymentIntent.invoiceObject, user)

		call.respond(subscription.toApiSubscription())
	}

	get("promoCode/{promoCode}") {
		val promoCode = try {
			call.parameters["promoCode"]!!
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		try {
			val params = PromotionCodeListParams.builder()
				.setCode(promoCode)
				.addExpand("data.coupon")
				.setLimit(1)
				.setActive(true)
				.build()
			val code = PromotionCode.list(params).data[0].coupon

			call.respond(
				ApiPromoCode(
					promoCode,
					code.amountOff?.toInt(),
					code.percentOff?.toInt(),
				)
			)
		} catch (e: StripeException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.INVALID_PROMO_CODE.toHashMap(true))
		} catch (e: IndexOutOfBoundsException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.INVALID_PROMO_CODE.toHashMap(true))
		}
	}
}