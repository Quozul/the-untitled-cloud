package dev.quozul.servers.routes

import dev.quozul.servers.Parameter
import dev.quozul.servers.Parameters
import dev.quozul.servers.models.ServerParameters
import dev.quozul.servers.recreateServer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureParametersRoutes() {
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