package dev.quozul.servers.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.configureParametersRoutes() {
	get("/parameters") {
		call.response.status(HttpStatusCode.SeeOther)
	}

	put("/parameters") {
		call.response.status(HttpStatusCode.SeeOther)
	}
}