package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.SignatureVerificationException
import com.stripe.model.*
import com.stripe.net.Webhook
import dev.quozul.authentication.User
import dev.quozul.authentication.Users
import dev.quozul.servers.Server
import dev.quozul.servers.createContainer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.configureStripeWebhook() {
	val endpointSecret = environment!!.config.property("payments.stripe.endpointSecret").getString()

	// Handle Stripe webhook
	post("/webhook") {
		val payload: String = call.receiveText()
		val sigHeader: String? = call.request.header("Stripe-Signature")

		val event: Event = try {
			Webhook.constructEvent(payload, sigHeader, endpointSecret)
		} catch (e: SignatureVerificationException) {
			// Invalid signature
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		// Deserialize the nested object inside the event
		val dataObjectDeserializer: EventDataObjectDeserializer = event.dataObjectDeserializer

		val stripeObject: StripeObject = if (dataObjectDeserializer.getObject().isPresent) {
			dataObjectDeserializer.getObject().get()
		} else {
			call.response.status(HttpStatusCode.InternalServerError)
			return@post
		}

		when (event.type) {
			"invoice.paid" -> {
				stripeObject as Invoice
				val id = stripeObject.id

				/*
				 * The user should never be null at this point.
				 * But it could happen.
				 */
				val user = transaction {
					User.find {
						Users.stripeId eq stripeObject.customer
					}.first()
				}

				createContainer(user, stripeObject.subscription, "itzg/minecraft-server")
			}

			"invoice.payment_failed" -> {
				// If the payment fails or the customer does not have a valid payment method,
				// an invoice.payment_failed event is sent, the subscription becomes past_due.
				// Use this webhook to notify your user that their payment has
				// failed and to retrieve new card details.
			}

			"customer.subscription.deleted" -> {
				// handle subscription canceled automatically based
				// upon your subscription settings. Or if the user
				// cancels it.
			}
		}

		call.response.status(HttpStatusCode.NotImplemented)
	}
}