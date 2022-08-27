package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.SignatureVerificationException
import com.stripe.model.*
import com.stripe.model.Subscription
import com.stripe.net.Webhook
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.DockerContainer
import dev.quozul.database.models.*
import dev.quozul.servers.helpers.NameGenerator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
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

				newSuspendedTransaction {
					dev.quozul.database.models.Subscription.find {
						Subscriptions.stripeId eq stripeObject.subscription
					}.firstOrNull()?.let { subscription ->
						subscription.products.forEach { product ->
							val container = Container.new {
								this.name = NameGenerator.getRandomName()
							}

							val server = Server.new {
								this.container = container
							}

							val dockerContainer = DockerContainer.new(
								image = product.dockerImage,
								env = server.toEnvironmentVariables(),
							)

							transaction {
								container.dockerContainer = dockerContainer
							}
						}

						subscription.subscriptionStatus = SubscriptionStatus.ACTIVE
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

					// TODO: Stop containers
				}

				call.response.status(HttpStatusCode.OK)
			}

			"customer.subscription.deleted" -> {
				stripeObject as Subscription

				getSubscriptionFromStripeId(stripeObject.id)?.let { subscription ->
					transaction {
						subscription.subscriptionStatus = SubscriptionStatus.CANCELLED
					}

					subscription.containers.forEach { container ->
						container.dockerContainer?.remove()
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