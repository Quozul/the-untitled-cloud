package dev.quozul.servers.routes

import dev.quozul.database.models.getSubscriptionFromId
import dev.quozul.servers.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
				container?.id.toString(),
				it.subscriptionStatus,
				container?.name,
				container?.dockerContainer?.getPort(),
				ServerState.fromContainerState(container?.dockerContainer?.state),
			)

			call.respond(response)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	patch {
		call.response.status(HttpStatusCode.SeeOther)
	}
}