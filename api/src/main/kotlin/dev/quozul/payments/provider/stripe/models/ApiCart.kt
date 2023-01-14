package dev.quozul.payments.provider.stripe.models

import dev.quozul.database.enums.GameServerE
import kotlinx.serialization.Serializable

@Serializable
data class ApiCart(
	val cart: List<GameServerE>,
	val promo: String?,
)
