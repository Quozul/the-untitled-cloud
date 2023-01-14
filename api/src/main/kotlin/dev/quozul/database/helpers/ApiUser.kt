package dev.quozul.database.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ApiUser(
	val subscriptions: List<ApiSubscription>,

	val id: String, // TODO: Use UUID type
	val email: String,
	val tosAcceptDate: LocalDateTime,
	val communicationLanguage: String, // TODO: Use Locale type
	val isEmailVerified: Boolean,
)