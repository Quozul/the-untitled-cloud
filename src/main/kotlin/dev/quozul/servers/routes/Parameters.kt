package dev.quozul.servers.routes

import dev.quozul.database.helpers.ApiServer
import dev.quozul.database.models.Container
import dev.quozul.database.models.Subscription
import dev.quozul.database.models.findServerFromContainer
import dev.quozul.servers.helpers.NameGenerator
import dev.quozul.servers.models.ServerName
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

		ApiServer.findFromContainerId(serverId)?.let {
			call.respond(it)
		} ?: call.response.status(HttpStatusCode.NotFound)
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
			val subscription = Subscription.findById(serverId)!!
			subscription.name = name.name ?: NameGenerator.getRandomName()
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
			call.receive<ApiServer>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		transaction {
			val container = Container.findById(serverId)!!

//			container.subscription.name = parameters.name
//			container.containerTag = parameters.tag

			// TODO: Verify container.product is a Minecraft Server
			findServerFromContainer(container)?.let {
				it.version = parameters.version
				it.serverType = parameters.serverType
				it.forgeVersion = parameters.forgeVersion
				it.fabricLauncherVersion = parameters.fabricLauncherVersion
				it.fabricLoaderVersion = parameters.fabricLoaderVersion
				it.quiltLauncherVersion = parameters.quiltLauncherVersion
				it.quiltLoaderVersion = parameters.quiltLoaderVersion
				it.ftbModpackId = parameters.ftbModpackId
				it.ftbModpackVersionId = parameters.ftbModpackVersionId
				it.useAikar = parameters.useAikar
				it.jvmFlags = parameters.jvmFlags
			}
		}

		call.response.status(HttpStatusCode.OK)
	}
}