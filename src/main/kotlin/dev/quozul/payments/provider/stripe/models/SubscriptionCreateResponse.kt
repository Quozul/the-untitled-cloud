package dev.quozul.payments.provider.stripe.models

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionCreateResponse(
	val clientSecret: String,
)
