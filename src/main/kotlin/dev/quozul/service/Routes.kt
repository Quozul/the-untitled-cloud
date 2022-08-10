package dev.quozul.service

import com.github.dockerjava.api.exception.NotModifiedException
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.database.models.*
import dev.quozul.servers.models.Action
import dev.quozul.servers.models.Paginate
import dev.quozul.servers.models.ServerActionRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Route.configureServiceRoutes() {
	get {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val offset = (page * size).toLong();

		val (services, count) = transaction {
			val query = Containers.innerJoin(Subscriptions)
				.innerJoin(Users)
				.slice(Containers.columns)
				.select { (Users.id eq uuid) }
				.withDistinct()

			val services = Container.wrapRows(query)

			Pair(services.limit(size, offset).map { it.toApiContainer() }, services.count())
		}

		val lastPage = count <= (page + 1) * size

		val response = Paginate(
			services,
			page == 0,
			lastPage,
			count / size,
			count,
			page,
		)

		call.respond(response)
	}

	get("{serviceId}") {
		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findContainerWithOwnership(serviceId, ownerId)?.let {
			call.respond(
				transaction {
					it.toApiContainer()
				}
			)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	patch("{serviceId}") {
		val action = try {
			call.receive<ServerActionRequest>().action
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		try {
			findContainerWithOwnership(serviceId, ownerId)?.dockerContainer?.let {
				when (action) {
					Action.START -> {
						it.start()
					}

					Action.STOP -> {
						it.stop()
					}

					Action.RESTART -> {
						it.restart()
					}

					Action.RECREATE -> {
						it.recreate()
					}

					Action.RESET -> {
						it.reset()
					}
				}
				call.response.status(HttpStatusCode.NoContent)
			}
		} catch (_: NotModifiedException) {
			call.response.status(HttpStatusCode.InternalServerError)
			call.respond(AuthenticationErrors.NOT_MODIFIED.toHashMap(true))
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
		}
	}
}