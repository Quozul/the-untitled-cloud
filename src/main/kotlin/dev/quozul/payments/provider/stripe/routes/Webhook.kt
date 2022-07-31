package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.SignatureVerificationException
import com.stripe.model.*
import com.stripe.net.Webhook
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

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

		// Handle the event
		println(event.type)
		when (stripeObject) {
			is PaymentIntent -> {
				val id = stripeObject.id

				when (event.type) {
					"invoice.paid" -> {
						// Used to provision services after the trial has ended.
						// The status of the invoice will show up as paid. Store the status in your
						// database to reference when a user accesses your service to avoid hitting rate
						// limits.
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

					else -> {
						println("Unhandled event type: " + event.type)
					}
				}
			}

			is Dispute -> {
				val id = stripeObject.paymentIntent

				when (event.type) {
					"charge.dispute.created" -> {
						//
					}

					"charge.dispute.funds_withdrawn" -> {
						//
					}

					else -> {
						println("Unhandled event type: " + event.type)
					}
				}
			}

			else -> {
				println("Unhandled event type: " + event.type)
			}
		}

		call.response.status(HttpStatusCode.NotImplemented)
	}
}