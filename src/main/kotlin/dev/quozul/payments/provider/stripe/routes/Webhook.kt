package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.SignatureVerificationException
import com.stripe.model.*
import com.stripe.model.Subscription
import com.stripe.net.Webhook
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.*
import dev.quozul.payments.provider.stripe.getUserFromStripeId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Route.configureStripeWebhook() {
	val endpointSecret = environment!!.config.property("payments.stripe.endpointSecret").getString()

	// Handle Stripe webhook
	post("/webhook") {
		val payload: String = call.receiveText()
		val sigHeader: String? = call.request.header("Stripe-Signature")

		val event: Event = try {
			Webhook.constructEvent(payload, sigHeader, endpointSecret)
		} catch (e: SignatureVerificationException) {
			e.printStackTrace()
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

				// Find subscription from Stripe Id
				val subscription = try {
					val user = getUserFromStripeId(stripeObject.customer)!!
					getOrCreateSubscriptionFromInvoice(stripeObject, user, SubscriptionStatus.ACTIVE)
				} catch (_: NullPointerException) {
					call.response.status(HttpStatusCode.BadRequest)
					return@post
				}

				// Create containers for all products
				runBlocking {
					newSuspendedTransaction {
						subscription.items.forEach { item ->
							item.createContainer()
						}
					}
				}

				call.response.status(HttpStatusCode.OK)
			}

			"invoice.payment_failed" -> {
				stripeObject as Invoice

				getSubscriptionFromStripeId(stripeObject.id)?.let { subscription ->
					transaction {
						subscription.subscriptionStatus = SubscriptionStatus.SUSPENDED
					}

					// Stop all containers
					subscription.containers.forEach { container ->
						container.dockerContainer?.stop()
					}
				}

				call.response.status(HttpStatusCode.OK)
			}

			"customer.subscription.deleted" -> {
				stripeObject as Subscription

				getSubscriptionFromStripeId(stripeObject.id)?.let { subscription ->
					// Create containers for all products
					runBlocking {
						newSuspendedTransaction {
							subscription.items.forEach { item ->
								item.remove()
							}

							subscription.subscriptionStatus = SubscriptionStatus.CANCELLED
							subscription.deletionDate = LocalDateTime.now()
						}
					}
				}

				call.response.status(HttpStatusCode.OK)
			}

			else -> {
				call.response.status(HttpStatusCode.NotImplemented)
			}
		}
	}
}