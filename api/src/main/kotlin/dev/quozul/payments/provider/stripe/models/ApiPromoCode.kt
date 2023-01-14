package dev.quozul.payments.provider.stripe.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiPromoCode(
	val code: String,
	val amountOff: Int?,
	val percentOff: Int?,
)
