package dev.quozul.servers.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.configureServerSubscriptionRoutes() {
	route("subscription") {
		get {
			call.response.status(HttpStatusCode.SeeOther)
		}

		delete {
			call.response.status(HttpStatusCode.SeeOther)
		}
	}
}