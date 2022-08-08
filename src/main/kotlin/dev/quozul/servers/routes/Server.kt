package dev.quozul.servers.routes

import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.ExposedPort
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.dockerClient
import dev.quozul.servers.Parameter
import dev.quozul.servers.Parameters
import dev.quozul.servers.Server
import dev.quozul.servers.models.*
import dev.quozul.servers.recreateServer
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

		val server = try {
			transaction {
				Server.findById(serverId)!!
			}
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.NotFound)
			return@get
		}

		// The following line might throw an error sometimes
		val container = try {
			dockerClient.inspectContainerCmd(server.containerId!!).exec()
		} catch (_: NotFoundException) {
			null
		} catch (_: NullPointerException) {
			null
		}

		val parameters = transaction {
			Parameter.find {
				Parameters.server eq serverId
			}.first()
		}

		val exposedPort = ExposedPort.tcp(25565)
		val port = container?.let { it.networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec }
		val state = ServerState.fromContainerState(container?.state)

		val response = DetailedApiServer(
			serverId.toString(),
			server.subscriptionStatus,
			server.containerName,
			port,
			state,
			ServerParameters.fromParameterEntity(parameters),
		)

		call.respond(response)
	}

	patch {
		val action = try {
			call.receive<ServerActionRequest>().action
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val server = try {
			val serverId = UUID.fromString(call.parameters["serverId"]!!)
			transaction {
				Server.findById(serverId)!!
			}
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		when (action) {
			Action.START -> {
				try {
					dockerClient.startContainerCmd(server.containerId!!).exec()
				} catch (_: NullPointerException) {
					call.response.status(HttpStatusCode.NotFound)
					call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
					return@patch
				}
				call.response.status(HttpStatusCode.NoContent)
			}

			Action.STOP -> {
				try {
					dockerClient.stopContainerCmd(server.containerId!!).exec()
				} catch (_: NotModifiedException) {
				} catch (_: NullPointerException) {
					call.response.status(HttpStatusCode.NotFound)
					call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
					return@patch
				}
				call.response.status(HttpStatusCode.NoContent)
			}

			Action.RESTART -> {
				try {
					dockerClient.restartContainerCmd(server.containerId!!).exec()
				} catch (_: NotModifiedException) {
				} catch (_: NullPointerException) {
					call.response.status(HttpStatusCode.NotFound)
					call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
					return@patch
				}
				call.response.status(HttpStatusCode.NoContent)
			}

			Action.RECREATE -> {
				recreateServer(server.id.value)
				call.response.status(HttpStatusCode.NoContent)
			}

			Action.RESET -> {
				call.response.status(HttpStatusCode.NotImplemented)
			}
		}
	}
}