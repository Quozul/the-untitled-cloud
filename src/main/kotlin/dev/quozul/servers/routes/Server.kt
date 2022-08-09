package dev.quozul.servers.routes

import com.github.dockerjava.api.exception.NotModifiedException
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.database.helpers.ApiContainer.Companion.fromContainerId
import dev.quozul.database.models.findContainerFromId
import dev.quozul.database.models.getSubscriptionFromId
import dev.quozul.servers.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureServerRoutes() {
	get {
		val serverId = try {
			UUID.fromString(call.parameters["serverId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		// The following line might throw an error sometimes
		getSubscriptionFromId(serverId)?.let {
			val container = transaction {
				it.containers.firstOrNull()
			}

			val response = V1DetailedApiServer(
				it.id.toString(),
				it.subscriptionStatus,
				it.name,
				container?.dockerContainer?.getPort(),
				ServerState.fromContainerState(container?.dockerContainer?.state),
			)

			call.respond(response)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	patch {
		val action = try {
			call.receive<ServerActionRequest>().action
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val server = try {
			UUID.fromString(call.parameters["serverId"]!!)
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		try {
			findContainerFromId(server)?.dockerContainer?.let {
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
			// TODO: Handle this error properly
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
			return@patch
		}
	}
}