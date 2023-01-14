package dev.quozul.authentication.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiDiscordCode(
	val code: String,
	val redirectUri: String,
)

@Serializable
data class DiscordToken(
	val access_token: String,
	val expires_in: Long,
	val refresh_token: String,
	val scope: String,
	val token_type: String,
)

@Serializable
data class DiscordUser(
	val id: String,
	val username: String,
	val discriminator: String,
	val avatar: String?,
	val bot: Boolean? = null,
	val system: Boolean? = null,
	val mfa_enabled: Boolean? = null,
	val banner: String? = null,
	val accent_color: Int? = null,
	val locale: String? = null,
	val verified: Boolean? = null,
	val email: String? = null,
	val flags: Int? = null,
	val premium_type: Int? = null,
	val public_flags: Int? = null,

	val avatar_decoration: String? = null,
	val banner_color: String? = null,
)

@Serializable
data class ApiDiscordUser(
	val id: String,
	val username: String,
	val discriminator: String,
	val avatar: String?,
)