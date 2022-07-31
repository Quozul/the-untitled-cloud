package dev.quozul.plugins

import dev.quozul.authentication.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureDatabase() {
    Database.connect(
        url = this@configureDatabase.environment.config.property("database.url").getString(),
        driver = this@configureDatabase.environment.config.property("database.driver").getString(),
        user = this@configureDatabase.environment.config.property("database.user").getString(),
        password = this@configureDatabase.environment.config.property("database.password").getString()
    )

    transaction {
        SchemaUtils.create(Users)
    }
}