package dev.quozul

import io.ktor.server.application.*
import dev.quozul.plugins.*
import dev.quozul.servers.getDockerClient
import dev.quozul.user.configureSmtp
import javax.mail.Session


val dockerClient = getDockerClient()
lateinit var smtpSession: Session
lateinit var containerDirectory: String

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    containerDirectory = this.environment.config.property("docker.directory").getString()
    smtpSession = configureSmtp()
    configurePayments()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabase()
    configureWebSockets()
    configureRouting()
}
