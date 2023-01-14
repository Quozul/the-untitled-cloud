package dev.quozul.user.models

import dev.quozul.authentication.models.ApiDiscordUser
import kotlinx.serialization.Serializable

@Serializable
data class ApiUser(
	val email: String,
	val communicationLanguage: String,
	val discord: ApiDiscordUser?,
)
