package dev.quozul.user

import dev.quozul.authentication.models.ApiDiscordUser
import dev.quozul.client
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

//val token = environment!!.config.property("discord.token").getString()
const val token = ""

suspend fun getDiscordUser(discordId: String) = client.get("https://discord.com/api/users/${discordId}") {
	contentType(ContentType.Application.Json)
	headers {
		append("authorization", "Bot $token")
	}
}.body<ApiDiscordUser>()