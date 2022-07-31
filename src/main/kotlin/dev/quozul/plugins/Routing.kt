package dev.quozul.plugins

import dev.quozul.authentication.configureAuthenticationRoutes
import dev.quozul.payments.provider.stripe.routes.configureStripeWebhook
import dev.quozul.payments.provider.stripe.routes.configureSubscriptionRoutes
import dev.quozul.user.configureUserRoutes
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

        route("/user") {
            configureUserRoutes()
        }

        route("/payment") {
            route("/stripe") {
                route("/subscription") {
                    authenticate {
                        configureSubscriptionRoutes()
                    }
                }
                configureStripeWebhook()
            }
        }
    }
}
