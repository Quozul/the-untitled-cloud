package dev.quozul.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.authentication.models.SignInCredentials
import dev.quozul.authentication.models.SignUpCredentials
import dev.quozul.database.models.User
import dev.quozul.database.models.Users
import dev.quozul.user.sendEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

fun Route.configureAuthenticationRoutes() {
	val secret = environment!!.config.property("jwt.secret").getString()
	val issuer = environment!!.config.property("jwt.domain").getString()
	val audience = environment!!.config.property("jwt.audience").getString()

	val salt = environment!!.config.property("passwords.salt").getString()
	val pepper = environment!!.config.property("passwords.pepper").getString()

	val isDevelopmentMode =
		(environment!!.config.propertyOrNull("ktor.development")?.getString() ?: "false").toBooleanStrict()

	fun generateJWT(user: User): String {
		return JWT.create()
			.withAudience(audience)
			.withIssuer(issuer)
			.withClaim("id", user.id.value.toString())
			.withClaim("email", user.email)
			.withExpiresAt(Date(System.currentTimeMillis() + 86400000 /* One day */))
			.sign(Algorithm.HMAC256(secret))
	}

	post("signUp") {
		val credentials = try {
			call.receive<SignUpCredentials>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		// TODO: Find a better way to hash password
		val hash = hashString("SHA-256", salt + credentials.password + pepper)

		// Find if a user already exists with these credentials
		val previousUser = transaction {
			User.find {
				Users.email eq credentials.email
			}.firstOrNull()
		}

		if (previousUser != null) {
			call.response.status(HttpStatusCode.Conflict)
			return@post
		}

		// Verify user has accepted the TOS
		if (!credentials.acceptTos) {
			call.response.status(HttpStatusCode.BadRequest)
			call.respond(AuthenticationErrors.ACCEPT_TOS.toHashMap())
			return@post
		}

		try {
			// "By signing you acknowledge to have read and accepted the TOS" -> set tosAcceptDate to now
			val user = transaction {
				User.new {
					password = hash
					email = credentials.email
					verificationCode = generateValidationCode()
					verificationCodeValidDate = LocalDateTime.now().plusHours(1)
					communicationLanguage = credentials.language
				}
			}

			// TODO: Send verification email according language
			// TODO: Use template for email
			if (!isDevelopmentMode) {
				sendEmail(
					credentials.email,
					"Votre code de vérification",
					"Voici votre code de vérification : '${user.verificationCode}'. Valable 1 heure."
				)
			} else {
				println("Email send with code ${user.verificationCode}")
			}

			call.respond(AuthenticationErrors.VERIFY_ACCOUNT.toHashMap())
			call.response.status(HttpStatusCode.Created)
			return@post
		} catch (e: ExposedSQLException) {
			call.response.status(HttpStatusCode.InternalServerError)
			return@post
		}
	}

	post("/code/{email}") {
		val email = try {
			call.parameters["email"]!!
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		// "By signing you acknowledge to have read and accepted the TOS" -> set tosAcceptDate to now
		val user = try {
			transaction {
				val user = User.find { Users.email eq email }.first()

				user.verificationCode = generateValidationCode()
				user.verificationCodeValidDate = LocalDateTime.now().plusHours(1)

				user
			}
		} catch (e: NoSuchElementException) {
			call.response.status(HttpStatusCode.NotFound)
			return@post
		}

		// TODO: Same as above
		if (!isDevelopmentMode) {
			sendEmail(
				email,
				"Votre code de vérification",
				"Voici votre code de vérification : '${user.verificationCode}'. Valable 1 heure."
			)
		} else {
			println("Email send with code ${user.verificationCode}")
		}

		call.response.status(HttpStatusCode.NoContent)
	}

	post("/password") {
		val credentials = try {
			call.receive<SignInCredentials>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		if (credentials.code == null) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		val user = try {
			transaction {
				User.find { Users.email eq credentials.email }.first()
			}
		} catch (e: NoSuchElementException) {
			call.response.status(HttpStatusCode.NotFound)
			return@post
		}

		// If a code is provided, verify it
		if (credentials.code != user.verificationCode) {
			// If the code is not valid, unauthorized
			call.response.status(HttpStatusCode.Unauthorized)
			call.respond(AuthenticationErrors.INVALID_CODE.toHashMap(true))
			return@post
		} else if (user.verificationCodeValidDate < LocalDateTime.now()) {
			// If the code is not valid, unauthorized
			call.response.status(HttpStatusCode.Unauthorized)
			call.respond(AuthenticationErrors.EXPIRED_CODE.toHashMap(true))
			return@post
		} else {
			// At this point, the code is valid
			val hash = hashString("SHA-256", salt + credentials.password + pepper)
			transaction {
				user.password = hash // Reset password
				user.verificationCodeValidDate = LocalDateTime.now() // Invalidate the code
			}
		}

		val token = generateJWT(user)
		call.respond(hashMapOf("token" to token))
	}

	post("/signIn") {
		val credentials = try {
			call.receive<SignInCredentials>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@post
		}

		// TODO: Find a better way to hash password
		val hash = hashString("SHA-256", salt + credentials.password + pepper)

		val user = transaction {
			User.find {
				(Users.email eq credentials.email) and (Users.password eq hash)
			}.firstOrNull()
		}

		user?.let {
			credentials.code?.let { code ->
				// If a code is provided, verify it
				if (code != user.verificationCode) {
					// If the code is not valid, unauthorized
					call.response.status(HttpStatusCode.Unauthorized)
					call.respond(AuthenticationErrors.INVALID_CODE.toHashMap(true))
					return@post
				} else if (user.verificationCodeValidDate < LocalDateTime.now()) {
					// If the code is not valid, unauthorized
					call.response.status(HttpStatusCode.Unauthorized)
					call.respond(AuthenticationErrors.EXPIRED_CODE.toHashMap(true))
					return@post
				} else {
					// At this point, the code is valid
					transaction {
						user.emailVerified = true
						user.verificationCodeValidDate = LocalDateTime.now() // Invalidate the code
					}
				}
			} ?: run {
				if (!it.emailVerified) {
					call.response.status(HttpStatusCode.Unauthorized)
					call.respond(AuthenticationErrors.VERIFY_ACCOUNT.toHashMap(true))
					return@post
				}
			}

			val token = generateJWT(user)
			call.respond(hashMapOf("token" to token))
		} ?: run {
			call.response.status(HttpStatusCode.BadRequest)
			call.respond(AuthenticationErrors.INVALID_CREDENTIALS.toHashMap(true))
		}
	}
}