package dev.quozul.payments.provider.stripe.models

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionCancelRequest(
	val subscriptionId: String,
)
