package dev.quozul.servers

import dev.quozul.authentication.models.UserCredentials
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
				Servers.owner eq uuid
			}
				.limit(size, (page * size).toLong())
				.map {
					val container = dockerClient.inspectContainerCmd(it.containerId).exec()
					ApiServer(it.id.toString(), container.state.toString(), container.name)
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
				call.parameters["serverId"]!!
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@get
			}

			val container = transaction {
				Server.findById(UUID.fromString(serverId))
			}?.let {
				dockerClient.inspectContainerCmd(it.containerId).exec()
			}

			if (container == null) {
				call.response.status(HttpStatusCode.NotFound)
				return@get
			}

			val response = ServerStatusApi(container.state?.status ?: "Unknown")
			call.respond(response)
		}

		post {
			val action = try {
				call.receive<ServerActionRequest>().action
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@post;
			}

			val serverId = call.parameters["serverId"] ?: run {
				call.response.status(HttpStatusCode.NotFound)
				return@post
			}

			val containerId = transaction {
				Server.findById(UUID.fromString(serverId))?.containerId
			} ?: run {
				call.response.status(HttpStatusCode.NotFound)
				return@post
			}

			when (action) {
				START -> {
						dockerClient.startContainerCmd(containerId).exec()
						call.response.status(HttpStatusCode.NoContent)
				}
				STOP -> {
						dockerClient.stopContainerCmd(containerId).exec()
						call.response.status(HttpStatusCode.NoContent)
				}
				RECREATE -> {
					call.response.status(HttpStatusCode.NotImplemented)
				}
			}
		}
	}
}