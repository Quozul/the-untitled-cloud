package dev.quozul.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.quozul.authentication.models.UserCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureAuthenticationRoutes() {
    val secret = environment!!.config.property("jwt.secret").getString()
    val issuer = environment!!.config.property("jwt.domain").getString()
    val audience = environment!!.config.property("jwt.audience").getString()
    val realm = environment!!.config.property("jwt.realm").getString()

    fun generateJWT(id: UUID): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", id.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000 /* One day */))
            .sign(Algorithm.HMAC256(secret));
    }

    post("signUp") {
        val credentials = try {
            call.receive<UserCredentials>()
        } catch (e: SerializationException) {
            call.response.status(HttpStatusCode.BadRequest)
            return@post;
        }

        // TODO: Find a better way to hash password
        val hash = hashString("SHA-256", credentials.password)

        val previousUser = transaction {
            User.find {
                Users.email eq credentials.email
            }.firstOrNull()
        }

        if (previousUser != null) {
            call.response.status(HttpStatusCode.Conflict)
            return@post;
        }

        try {
            // "By signing you acknowledge to have read and accepted the TOS" -> set tosAcceptDate to now
            val user = transaction {
                User.new {
                    password = hash
                    email = credentials.email
                }
            }

            // TODO: Send email verification

            user.let {
                val token = generateJWT(user.id.value)
                call.respond(hashMapOf("token" to token))
            }
        } catch (e: ExposedSQLException) {
            when (e.cause) {
                else -> {
                    call.response.status(HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    post("/signIn") {
        val credentials = try {
            call.receive<UserCredentials>()
        } catch (e: SerializationException) {
            call.response.status(HttpStatusCode.BadRequest)
            return@post;
        }

        // TODO: Find a better way to hash password
        val hash = hashString("SHA-256", credentials.password)

        val user = transaction {
            User.find {
                (Users.email eq credentials.email) and (Users.password eq hash)
            }.firstOrNull()
        }

        // TODO: Check if user has validated their email
        // TODO: Check if user has accepted latest TOS

        user?.let {
            val token = generateJWT(user.id.value)
            call.respond(hashMapOf("token" to token))
        } ?: run {
            call.response.status(HttpStatusCode.BadRequest)
        }
    }
}