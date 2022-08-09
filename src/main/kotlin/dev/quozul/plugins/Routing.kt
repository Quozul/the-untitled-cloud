package dev.quozul.plugins

import dev.quozul.authentication.configureAuthenticationRoutes
import dev.quozul.payments.provider.stripe.routes.configureStripeWebhook
import dev.quozul.payments.provider.stripe.routes.configureServerSubscriptionRoutes
import dev.quozul.products.configureProductsRoutes
import dev.quozul.servers.configureServersRoutes
import dev.quozul.servers.routes.configureConsoleWebsocket
import dev.quozul.subscriptions.configureSubscriptionRoutes
import dev.quozul.user.configureUserRoutes
import dev.quozul.versions.configureVersionRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureRouting() {
	routing {
		get("/health") {
			call.respondText("Ok")
		}

		route("/authentication") {
			configureAuthenticationRoutes()
		}

		route("/products") {
			configureProductsRoutes()
		}

		route("/user") {
			configureUserRoutes()
		}

		route("/subscription") {
			authenticate {
				configureSubscriptionRoutes()
			}
		}

		route("/payment") {
			route("/stripe") {
				route("/subscription") {
					authenticate {
						configureServerSubscriptionRoutes()
					}
				}
				configureStripeWebhook()
			}
		}

		route("/server") {
			configureConsoleWebsocket()

			authenticate {
				configureServersRoutes()
			}
		}

		route("/versions") {
			configureVersionRoutes()
		}
	}
}
