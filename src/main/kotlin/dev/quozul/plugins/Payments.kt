package dev.quozul.plugins

import com.stripe.Stripe
import io.ktor.server.application.*

fun Application.configurePayments() {
    val apiKey = this@configurePayments.environment.config.property("payments.stripe.apiKey").getString()
    Stripe.apiKey = apiKey
}
