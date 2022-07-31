package dev.quozul.servers

import dev.quozul.servers.models.ApiServer
import dev.quozul.servers.models.Paginate
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                    ApiServer(it.id.toString(), it.subscriptionId, it.containerId)
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
}