package dev.quozul.plugins

import dev.quozul.database.models.*
import dev.quozul.payments.provider.stripe.ProductPrices
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
        SchemaUtils.create(Containers)
        SchemaUtils.create(Options)
        SchemaUtils.create(Products)
        SchemaUtils.create(Servers)
        SchemaUtils.create(Subscriptions)
        SchemaUtils.create(SubscriptionItems)
        SchemaUtils.create(Users)

        println(SchemaUtils.statementsRequiredToActualizeScheme(Containers))
        println(SchemaUtils.statementsRequiredToActualizeScheme(Options))
        println(SchemaUtils.statementsRequiredToActualizeScheme(Products))
        println(SchemaUtils.statementsRequiredToActualizeScheme(Servers))
        println(SchemaUtils.statementsRequiredToActualizeScheme(Subscriptions))
        println(SchemaUtils.statementsRequiredToActualizeScheme(SubscriptionItems))
        println(SchemaUtils.statementsRequiredToActualizeScheme(Users))
    }
}