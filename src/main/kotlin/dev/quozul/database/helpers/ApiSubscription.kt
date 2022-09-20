package dev.quozul.database.helpers

import com.stripe.param.SubscriptionRetrieveParams
import com.stripe.model.Subscription as StripeSubscription
import dev.quozul.database.enums.SubscriptionProvider
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.Subscription
import dev.quozul.servers.models.ApiInvoice
import dev.quozul.servers.models.SubscriptionInfo
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
data class ApiSubscription(
	val id: String,
	val status: SubscriptionStatus,
	val provider: SubscriptionProvider,
	val creationDate: LocalDateTime,
	val deletionDate: LocalDateTime?,
	val active: Boolean,
) {
	companion object {
		fun getDetails(subscription: Subscription) = try {
			val params = SubscriptionRetrieveParams.builder()
				.addAllExpand(listOf("default_payment_method", "latest_invoice")).build()
			val stripeSubscription = StripeSubscription.retrieve(subscription.stripeId, params, null)
			val paymentMethod = stripeSubscription.defaultPaymentMethodObject
			val invoice = stripeSubscription.latestInvoiceObject

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

			SubscriptionInfo(
				Instant.fromEpochSeconds(stripeSubscription.startDate),
				Instant.fromEpochSeconds(stripeSubscription.currentPeriodStart),
				Instant.fromEpochSeconds(stripeSubscription.currentPeriodEnd),
				stripeSubscription.canceledAt?.let { Instant.fromEpochSeconds(stripeSubscription.canceledAt) },
				stripeSubscription.status,
				stripeSubscription.cancelAtPeriodEnd,
				paymentMethod?.type,
				paymentMethod?.card?.last4 ?: paymentMethod?.sepaDebit?.last4,
				latestInvoice,
			)
		} catch (e: SerializationException) {
			null
		}
	}
}