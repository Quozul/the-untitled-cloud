package dev.quozul.payments.provider.stripe.routes

import com.stripe.model.Subscription
import com.stripe.param.SubscriptionCreateParams
import dev.quozul.authentication.User
import dev.quozul.payments.provider.stripe.ProductPrices
import dev.quozul.payments.provider.stripe.getOrCreateStripeCustomer
import dev.quozul.payments.provider.stripe.models.SubscriptionCreateResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

			val subscription = Subscription.create(subCreateParams)

			val response = SubscriptionCreateResponse(
				subscription.id,
				subscription.latestInvoiceObject.paymentIntentObject.clientSecret,
			)

			call.response.status(HttpStatusCode.Created)
			call.respond(response)
		}
	}

	delete("{subscriptionId}") {
		val subscriptionId = call.parameters["subscriptionId"]
		val subscription = Subscription.retrieve(subscriptionId)
		subscription.cancel()

		call.response.status(HttpStatusCode.NoContent)
	}
}