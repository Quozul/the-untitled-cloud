package dev.quozul.plugins

import dev.quozul.authentication.configureAuthenticationRoutes
import dev.quozul.payments.provider.stripe.routes.configurePromoCodes
import dev.quozul.payments.provider.stripe.routes.configureStripeWebhook
import dev.quozul.payments.provider.stripe.routes.configureServerSubscriptionRoutes
import dev.quozul.products.configureProductsRoutes
import dev.quozul.servers.routes.configureConsoleWebsocket
import dev.quozul.service.configureServiceRoutes
import dev.quozul.subscription.configureSubscriptionRoutes
import dev.quozul.user.configureUserRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureRouting() {
	routing {
		// Routes v1
		get("/health") {
			call.respondText("Ok")
		}

		route("/authentication") {
			configureAuthenticationRoutes()
		}

		route("/payment") {
			route("/stripe") {
				route("/subscription") {
					authenticate {
						configureServerSubscriptionRoutes()
					}
				}
				configurePromoCodes()
				configureStripeWebhook()
			}
		}

		route("/server") {
			configureConsoleWebsocket()
		}

		// Routes v2
		route("/product") {
			configureProductsRoutes()
		}

		authenticate {
			route("/subscription") {
				configureSubscriptionRoutes()
			}

			route("/service") {
				configureServiceRoutes()
			}

			// Routes v1
			route("/user") {
				configureUserRoutes()
			}
		}
	}
}
