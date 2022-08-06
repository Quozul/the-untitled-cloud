package dev.quozul.servers

import dev.quozul.servers.models.*
import dev.quozul.servers.routes.configureParametersRoutes
import dev.quozul.servers.routes.configureServerRoutes
import dev.quozul.servers.routes.configureServerSubscriptionRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Route.configureServersRoutes() {
	get("") {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val status = call.request.queryParameters["status"]

		val servers = transaction {
			val servers = when (status) {
				"ENDED" -> {
					Server.find {
						(Servers.owner eq uuid) and (Servers.status eq ServerStatus.ENDED)
					}
						.limit(size, (page * size).toLong())
				}

				else -> {
					Server.find {
						(Servers.owner eq uuid) and (Servers.status neq ServerStatus.ENDED)
					}
						.limit(size, (page * size).toLong())
				}
			}

			servers.map { ApiServer.fromServer(it) }
		}

		val count = transaction {
			Server.count()
		}

		val lastPage = count <= (page + 1) * size

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
		configureServerRoutes()

		configureParametersRoutes()

		configureServerSubscriptionRoutes()
	}
}