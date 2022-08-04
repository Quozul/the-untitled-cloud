package dev.quozul.servers.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiInvoice(
	val periodStart: Instant,
	val periodEnd: Instant,
	val paid: Boolean,
	val amountDue: Long,
	val amountPaid: Long,
	val amountRemaining: Long,
	val total: Long,
	val currency: String,
)

@Serializable
data class SubscriptionInfo(
	val startDate: Instant,
	val currentPeriodStart: Instant,
	val currentPeriodEnd: Instant,
	val canceledAt: Instant?,
	val status: String,
	val cancelAtPeriodEnd: Boolean,
	val paymentMethodType: String,
	val paymentMethodLast4: String?,
	val latestInvoice: ApiInvoice,
)
