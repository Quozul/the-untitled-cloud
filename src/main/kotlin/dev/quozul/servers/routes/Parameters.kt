package dev.quozul.servers.routes

import dev.quozul.servers.Parameter
import dev.quozul.servers.Parameters
import dev.quozul.servers.Server
import dev.quozul.servers.helpers.NameGenerator
import dev.quozul.servers.models.ServerName
import dev.quozul.servers.models.ServerParameters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.NoSuchElementException

fun Route.configureParametersRoutes() {
	get("/parameters") {
		val serverId = try {
			UUID.fromString(call.parameters["serverId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		try {
			call.respond(ServerParameters.fromServerId(serverId));
		} catch (_: NoSuchElementException) {
			call.response.status(HttpStatusCode.NotFound)
		}
	}

	put("/name") {
		val serverId = try {
			UUID.fromString(call.parameters["serverId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val name = try {
			call.receive<ServerName>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		transaction {
			val server = Server.findById(serverId)!!
			server.containerName = name.name ?: NameGenerator.getRandomName()
		}

		call.response.status(HttpStatusCode.OK)
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
			return@put
		}

		transaction {
			val server = Server.findById(serverId)!!

			server.containerName = parameters.name
			server.containerTag = parameters.tag

			// TODO: Create if not exists
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
			parameter.useAikar = parameters.useAikar
			parameter.jvmFlags = parameters.jvmFlags
		}

		call.response.status(HttpStatusCode.OK)
	}
}