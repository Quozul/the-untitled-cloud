package dev.quozul.database.helpers

import com.stripe.param.SubscriptionRetrieveParams
import dev.quozul.database.enums.SubscriptionProvider
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.Subscription
import dev.quozul.database.models.Subscriptions
import dev.quozul.database.models.User
import dev.quozul.servers.models.ApiInvoice
import dev.quozul.servers.models.SubscriptionInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ApiSubscription(
	val id: String,
	val subscriptionStatus: SubscriptionStatus,
	val subscriptionProvider: SubscriptionProvider,
	val creationDate: LocalDateTime,
	val deletionDate: LocalDateTime?,
	val name: String?,

	// Expandable fields
	var products: List<ApiProduct>? = null,
	var containers: List<ApiContainer>? = null,
	var details: SubscriptionInfo? = null,
) {
	companion object {
		fun fromOwner(owner: User): List<ApiSubscription> = transaction {
			Subscription.find {
				Subscriptions.owner eq owner.id
			}
				.map {
					it.toApiSubscription()
				}
		}
	}

	fun expandDetails(): ApiSubscription {
		details = try {
			val uid = UUID.fromString(id)
			println(uid)
			val stripeId = transaction {
				Subscription.findById(uid)!!.stripeId
			}

			val params = SubscriptionRetrieveParams.builder()
				.addAllExpand(listOf("default_payment_method", "latest_invoice")).build()
			val subscription = com.stripe.model.Subscription.retrieve(stripeId, params, null)
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

			SubscriptionInfo(
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
		} catch (e: SerializationException) {
			null
		}

		return this
	}
}