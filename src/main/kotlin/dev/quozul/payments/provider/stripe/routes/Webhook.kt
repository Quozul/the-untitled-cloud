package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.SignatureVerificationException
import com.stripe.exception.StripeException
import com.stripe.model.*
import com.stripe.net.Webhook
import com.stripe.param.InvoiceRetrieveParams
import com.stripe.param.InvoiceVoidInvoiceParams
import dev.quozul.payments.provider.stripe.getUserFromStripeId
import dev.quozul.servers.createContainer
import dev.quozul.servers.deleteContainer
import dev.quozul.servers.suspendContainer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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

		// TODO: Send notification email
		when (event.type) {
			"invoice.paid" -> {
				stripeObject as Invoice

				getUserFromStripeId(stripeObject.customer)?.let {
					// TODO: Support other products and options
					createContainer(it, stripeObject.subscription)
					call.response.status(HttpStatusCode.OK)
				} ?: call.response.status(HttpStatusCode.BadRequest)
			}

			"invoice.payment_failed" -> {
				stripeObject as Invoice

				getUserFromStripeId(stripeObject.customer)?.let {
					suspendContainer(it, stripeObject.subscription)
					call.response.status(HttpStatusCode.OK)
				} ?: call.response.status(HttpStatusCode.BadRequest)
			}

			"customer.subscription.deleted" -> {
				stripeObject as Subscription

				getUserFromStripeId(stripeObject.customer)?.let {
					deleteContainer(it, stripeObject.id)
					call.response.status(HttpStatusCode.OK)
				} ?: run {
					call.respondText("Cannot find user")
					call.response.status(HttpStatusCode.BadRequest)
				}
			}

			else -> {
				call.response.status(HttpStatusCode.NotImplemented)
			}
		}
	}
}