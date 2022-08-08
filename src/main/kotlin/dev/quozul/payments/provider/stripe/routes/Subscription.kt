package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.StripeException
import com.stripe.model.Invoice
import com.stripe.model.PaymentIntent
import com.stripe.model.Subscription
import com.stripe.param.SubscriptionCreateParams
import dev.quozul.authentication.User
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.payments.provider.stripe.ProductPrices
import dev.quozul.payments.provider.stripe.getOrCreateStripeCustomer
import dev.quozul.payments.provider.stripe.models.ApiPaymentIntentUpdate
import dev.quozul.payments.provider.stripe.models.SubscriptionCreateResponse
import dev.quozul.servers.SubscriptionServerStatus
import dev.quozul.servers.createOrUpdateServer
import dev.quozul.servers.findServerFromSubscription
import dev.quozul.servers.models.ApiServer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureSubscriptionRoutes() {
	// Subscription intent
	post("") {
		// We have only one product, so no input is expected

		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val user = transaction {
			User.findById(UUID.fromString(id))
		}

		// Get the Stripe customer from the database
		if (user != null) {
			val customer = getOrCreateStripeCustomer(user)

			val paymentSettings = SubscriptionCreateParams.PaymentSettings.builder()
				.setSaveDefaultPaymentMethod(SubscriptionCreateParams.PaymentSettings.SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
				.build()

			val item = SubscriptionCreateParams.Item.builder()
				.setPrice(ProductPrices.MINECRAFT_SERVER_BASIC.priceId)
				.build()

			val subCreateParams = SubscriptionCreateParams.builder()
				.setCustomer(customer.id)
				.addItem(item)
				.setPaymentSettings(paymentSettings)
				.setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
				.addAllExpand(listOf("latest_invoice.payment_intent"))
				.build()

			val subscription = try {
				Subscription.create(subCreateParams)
			} catch (_: StripeException) {
				call.response.status(HttpStatusCode.InternalServerError)
				call.respond(AuthenticationErrors.STRIPE_ERROR.toHashMap(true))
				return@post
			}

			val response = SubscriptionCreateResponse(
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

		val invoice = try {
			val paymentIntent = PaymentIntent.retrieve(body.paymentIntentId)
			Invoice.retrieve(paymentIntent.invoice)
		} catch (_: StripeException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		if (invoice.subscription != null) {
			val server = findServerFromSubscription(invoice.subscription) ?: createOrUpdateServer(user, invoice.subscription, SubscriptionServerStatus.PENDING)
			call.respond(ApiServer.fromServer(server))
		} else {
			call.response.status(HttpStatusCode.NotFound)
		}
	}
}