package dev.quozul.database.extensions.subscriptionItem

import dev.quozul.database.enums.ContainerStatus
import dev.quozul.database.enums.GameServerE
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.DockerContainer
import dev.quozul.database.models.SubscriptionItem
import org.jetbrains.exposed.sql.transactions.transaction


var SubscriptionItem.dockerContainer: DockerContainer?
	get() = transaction { container?.dockerContainer }
	set(value) = transaction {
		container?.dockerContainer = value
	}

val SubscriptionItem.port: String?
	get() = product.gameServer.ports.firstOrNull()?.let { exposedPort ->
		dockerContainer?.networkSettings?.ports?.bindings?.get(exposedPort)?.first()?.hostPortSpec
	}

val SubscriptionItem.isRunning: Boolean
	get() = dockerContainer?.state?.running == true

val SubscriptionItem.gameServer: GameServerE
	get() = transaction { product.gameServer }

val SubscriptionItem.subscriptionIsActive: Boolean
	get() = transaction {
		subscription.subscriptionStatus === SubscriptionStatus.ACTIVE
	}

val SubscriptionItem.isActive: Boolean
	get() = transaction {
		(container?.containerStatus ?: true).let {
			it === ContainerStatus.CREATED || it === ContainerStatus.REGISTERED
		} && subscription.subscriptionStatus === SubscriptionStatus.ACTIVE
	}

var SubscriptionItem.containerStatus: ContainerStatus
	get() = transaction { container?.containerStatus } ?: ContainerStatus.UNAVAILABLE
	set(value) = transaction { container?.containerStatus = value }