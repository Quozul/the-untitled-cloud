package dev.quozul.servers.routes

import com.stripe.exception.StripeException
import com.stripe.model.Subscription
import com.stripe.param.SubscriptionCancelParams
import com.stripe.param.SubscriptionRetrieveParams
import com.stripe.param.SubscriptionUpdateParams
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.servers.Server
import dev.quozul.servers.ServerStatus
import dev.quozul.servers.models.ApiInvoice
import dev.quozul.servers.models.CancelMode
import dev.quozul.servers.models.SubscriptionInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureServerSubscriptionRoutes() {
	route("subscription") {
		get {
			val server = try {
				val serverId = UUID.fromString(call.parameters["serverId"]!!)
				transaction {
					Server.findById(serverId)!!
				}
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@get
			}

			try {
				val params = SubscriptionRetrieveParams.builder()
					.addAllExpand(listOf("default_payment_method", "latest_invoice")).build()
				val subscription = Subscription.retrieve(server.subscriptionId, params, null)
				val paymentMethod = subscription.defaultPaymentMethodObject
				val invoice = subscription.latestInvoiceObject

				val latestInvoice = ApiInvoice(
					Instant.fromEpochSeconds(invoice.periodStart),
					Instant.fromEpochSeconds(invoice.periodEnd),
					invoice.paid,
					invoice.amountDue,
					invoice.amountPaid,
					invoice.amountRemaining,
					invoice.total,
					invoice.currency,
				)

				val info = SubscriptionInfo(
					Instant.fromEpochSeconds(subscription.startDate),
					Instant.fromEpochSeconds(subscription.currentPeriodStart),
					Instant.fromEpochSeconds(subscription.currentPeriodEnd),
					subscription.canceledAt?.let { Instant.fromEpochSeconds(subscription.canceledAt) },
					subscription.status,
					subscription.cancelAtPeriodEnd,
					paymentMethod.type,
					paymentMethod.card?.last4 ?: paymentMethod.sepaDebit?.last4,
					latestInvoice,
				)

				call.respond(info)
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.NotFound)
				return@get
			}
		}

		delete {
			val server = try {
				val serverId = UUID.fromString(call.parameters["serverId"]!!)
				transaction {
					Server.findById(serverId)!!
				}
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@delete
			}

			val mode = try {
				call.receive<CancelMode>()
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@delete
			}

			val subscription = try {
				Subscription.retrieve(server.subscriptionId)
			} catch (_: StripeException) {
				call.response.status(HttpStatusCode.NotFound)
				return@delete
			}

			try {
				if (mode.now) {
					val params = SubscriptionCancelParams.builder()
						.setProrate(false) // TODO: Verify its in the 14 days trial
						.build()

					subscription.cancel(params)
					// TODO: Void invoice
				} else {
					val params = SubscriptionUpdateParams.builder()
						.setCancelAtPeriodEnd(true)
						.build()

					subscription.update(params)
				}

				transaction {
					server.status = ServerStatus.SUSPENDED
				}

				call.response.status(HttpStatusCode.NoContent)
			} catch (_: StripeException) {
				call.response.status(HttpStatusCode.InternalServerError)
				call.respond(AuthenticationErrors.CANCEL_ERROR.toHashMap(true))
				return@delete
			}
		}
	}
}