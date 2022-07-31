package dev.quozul

import io.ktor.server.application.*
import dev.quozul.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configurePayments()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureDatabase()
}
