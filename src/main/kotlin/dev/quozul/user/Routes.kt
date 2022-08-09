package dev.quozul.user

import dev.quozul.payments.provider.stripe.models.Address
import com.stripe.param.CustomerUpdateParams
import dev.quozul.database.models.User
import dev.quozul.payments.provider.stripe.getOrCreateStripeCustomer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Route.configureUserRoutes() {
	// Authenticated requests
	authenticate {
		// TODO: Add renew token
		// TODO: Add password change

		post("") {
			val principal = call.principal<JWTPrincipal>()
			val id = principal!!.payload.getClaim("id").asString()

			val email = transaction {
				User.findById(UUID.fromString(id))!!.email
			}

			sendEmail(email, "subject", "content")
			call.response.status(HttpStatusCode.NoContent)
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
}