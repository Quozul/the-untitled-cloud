package dev.quozul.servers

import dev.quozul.servers.routes.configureConsoleWebsocket
import dev.quozul.servers.routes.configureParametersRoutes
import dev.quozul.servers.routes.configureServerRoutes
import dev.quozul.servers.routes.configureServerSubscriptionRoutes
import io.ktor.server.routing.*


fun Route.configureServersRoutes() {
	route("{serverId}") {
		configureServerRoutes()

		configureConsoleWebsocket()

		configureParametersRoutes()

		configureServerSubscriptionRoutes()
	}
}