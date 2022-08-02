package dev.quozul.servers

import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.ExposedPort
import dev.quozul.dockerClient
import dev.quozul.servers.models.*
import dev.quozul.servers.models.Action.*
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


fun Route.configureServerRoutes() {
	get("") {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()

		val servers = transaction {
			Server.find {
				(Servers.owner eq uuid) and (Servers.containerId neq null)
			}
				.limit(size, (page * size).toLong())
				.map {
					val name = try {
						dockerClient.inspectContainerCmd(it.containerId!!).exec().name
					} catch (_: NullPointerException) {
						null
					}

					ApiServer(it.id.toString(), name, it.status)
				}
		}

		val count = transaction {
			Server.count()
		}

		val lastPage = count <= (page + 1) * size;

		val response = Paginate(
			servers,
			page == 0,
			lastPage,
			count / size,
			count,
		)

		call.respond(response)
	}

	route("{serverId}") {
		get {
			val serverId = try {
				UUID.fromString(call.parameters["serverId"]!!)
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@get
			}

			val containerId = try {
				transaction {
					Server.findById(serverId)?.containerId!!
				}
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.NotFound)
				return@get
			}

			// FIXME: The following line might throw an error sometimes
			val container = dockerClient.inspectContainerCmd(containerId).exec()

			if (container == null) {
				call.response.status(HttpStatusCode.NotFound)
				return@get
			}

			val parameters = transaction {
				Parameter.find {
					Parameters.server eq serverId
				}.first()
			}

			val exposedPort = ExposedPort.tcp(25565)
			val port = container.networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec

			val response = DetailedApiServer(
				serverId.toString(),
				container.name,
				port,
				ServerState.fromContainerState(container.state),
				ServerParameters.fromParameterEntity(parameters),
			)

			call.respond(response)
		}

		patch {
			val action = try {
				call.receive<ServerActionRequest>().action
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@patch;
			}

			val serverId = call.parameters["serverId"] ?: run {
				call.response.status(HttpStatusCode.NotFound)
				return@patch
			}

			val containerId = transaction {
				Server.findById(UUID.fromString(serverId))?.containerId
			} ?: run {
				call.response.status(HttpStatusCode.NotFound)
				return@patch
			}

			when (action) {
				START -> {
					dockerClient.startContainerCmd(containerId).exec()
					call.response.status(HttpStatusCode.NoContent)
				}

				STOP -> {
					try {
						dockerClient.stopContainerCmd(containerId).exec()
					} catch (_: NotModifiedException) {
					}
					call.response.status(HttpStatusCode.NoContent)
				}

				RESTART -> {
					try {
						dockerClient.restartContainerCmd(containerId).exec()
					} catch (_: NotModifiedException) {
					}
					call.response.status(HttpStatusCode.NoContent)
				}
			}
		}

		put("/parameters") {
			val serverId = try {
				UUID.fromString(call.parameters["serverId"]!!)
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@put
			}

			val parameters = try {
				call.receive<ServerParameters>()
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@put;
			}

			transaction {
				val parameter = Parameter.find {
					Parameters.server eq serverId
				}.first()

				parameter.version = parameters.version
				parameter.eula = parameters.eula
				parameter.serverType = parameters.serverType
				parameter.forgeVersion = parameters.forgeVersion
				parameter.fabricLauncherVersion = parameters.fabricLauncherVersion
				parameter.fabricLoaderVersion = parameters.fabricLoaderVersion
				parameter.quiltLauncherVersion = parameters.quiltLauncherVersion
				parameter.quiltLoaderVersion = parameters.quiltLoaderVersion
				parameter.ftbModpackId = parameters.ftbModpackId
				parameter.ftbModpackVersionId = parameters.ftbModpackVersionId
			}

			if (recreateServer(serverId)) {
				call.response.status(HttpStatusCode.OK)
			} else {
				call.response.status(HttpStatusCode.InternalServerError)
			}
		}
	}
}