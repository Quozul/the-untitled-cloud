package dev.quozul.plugins

import dev.quozul.database.models.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
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


		SchemaUtils.statementsRequiredToActualizeScheme(Containers) +
				SchemaUtils.statementsRequiredToActualizeScheme(Options) +
				SchemaUtils.statementsRequiredToActualizeScheme(Products) +
				SchemaUtils.statementsRequiredToActualizeScheme(Servers) +
				SchemaUtils.statementsRequiredToActualizeScheme(Subscriptions) +
				SchemaUtils.statementsRequiredToActualizeScheme(SubscriptionItems) +
				SchemaUtils.statementsRequiredToActualizeScheme(Users)
					.forEach {
						try {
							TransactionManager.current().exec(it)
						} catch (e: Exception) {
							e.printStackTrace()
						}
					}
	}
}