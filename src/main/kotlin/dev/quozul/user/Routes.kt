package dev.quozul.user

import dev.quozul.payments.provider.stripe.models.Address
import com.stripe.param.CustomerUpdateParams
import dev.quozul.authentication.hashString
import dev.quozul.authentication.models.*
import dev.quozul.client
import dev.quozul.database.models.User
import dev.quozul.database.models.Users
import dev.quozul.payments.provider.stripe.getOrCreateStripeCustomer
import dev.quozul.user.models.ApiUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Route.configureUserRoutes() {
	val token = this@configureUserRoutes.environment!!.config.property("discord.token").getString()
	val clientSecret = this@configureUserRoutes.environment!!.config.property("discord.client_secret").getString()
	val clientId = this@configureUserRoutes.environment!!.config.property("discord.client_id").getString()
	val salt = environment!!.config.property("passwords.salt").getString()
	val pepper = environment!!.config.property("passwords.pepper").getString()

	// Authenticated requests
	// TODO: Add renew token
	// TODO: Add password change

	get {
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val user = transaction {
			User.findById(UUID.fromString(id))!!
		}

		val discordUser = if (user.discordId != null) {
			client.get("https://discord.com/api/users/${user.discordId}") {
				contentType(ContentType.Application.Json)
				headers {
					append("authorization", "Bot $token")
				}
			}.body<ApiDiscordUser>()
		} else {
			null
		}

		call.respond(ApiUser(user.email, user.communicationLanguage, discordUser))
	}

	post {
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val email = transaction {
			User.findById(UUID.fromString(id))!!.email
		}

		sendEmail(email, "subject", "content")
		call.response.status(HttpStatusCode.NoContent)
	}

	delete {
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val credentials = try {
			call.receive<VerificationCredentials>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@delete
		}

		val hash = hashString("SHA-256", salt + credentials.password + pepper)

		transaction {
			User.find {
				(Users.id eq UUID.fromString(id)) and (Users.verificationCode eq credentials.code) and (Users.password eq hash)
			}.firstOrNull()
		}?.let { user ->
			call.response.status(HttpStatusCode.NoContent)
		} ?: run {
			call.response.status(HttpStatusCode.Unauthorized)
			call.respond(AuthenticationErrors.INVALID_CREDENTIALS.toHashMap(true))
		}
	}

	post("/discord") {
		val discordCode = try {
			call.receive<ApiDiscordCode>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		val principal = call.principal<JWTPrincipal>()
		val id = UUID.fromString(principal!!.payload.getClaim("id").asString())

		val user = try {
			transaction {
				User.findById(id)!!
			}
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.NotFound)
			return@post
		}

		val params = Parameters.build {
			append("client_secret", clientSecret)
			append("client_id", clientId)
			append("grant_type", "authorization_code")
			append("redirect_uri", discordCode.redirectUri)
			append("code", discordCode.code)
		}

		val tokenResponse = client.post("https://discord.com/api/oauth2/token") {
			contentType(ContentType.Application.FormUrlEncoded)
			setBody(params.formUrlEncode())
		}.body<DiscordToken>()

		val userResponse = client.get("https://discord.com/api/users/@me") {
			contentType(ContentType.Application.Json)
			bearerAuth(tokenResponse.access_token)
		}.body<DiscordUser>()

		transaction {
			user.discordId = userResponse.id
		}

		call.respond(userResponse)
	}

	// Get user billing address
	get("/address") {
		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		Address.fromUserId(UUID.fromString(id))?.let {
			call.respond(it)
		} ?: run {
			call.response.status(HttpStatusCode.NoContent)
		}
	}

	// Set user billing address
	post("/address") {
		val address = try {
			call.receive<Address>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		val principal = call.principal<JWTPrincipal>()
		val id = principal!!.payload.getClaim("id").asString()

		val user = transaction {
			User.findById(UUID.fromString(id))
		}

		if (user != null) {
			val customer = getOrCreateStripeCustomer(user)

			val addressParams = CustomerUpdateParams.Address.builder()
				.setCity(address.city)
				.setCountry(address.country)
				.setLine1(address.line1)
				.setLine2(address.line2)
				.setPostalCode(address.postal_code)
				.setState(address.state)
				.build()

			val params: CustomerUpdateParams = CustomerUpdateParams.builder()
				.setAddress(addressParams)
				.setName(address.fullname)
				.build()

			customer.update(params)

			call.response.status(HttpStatusCode.NoContent)
			return@post
		}

		call.response.status(HttpStatusCode.InternalServerError)
	}
}