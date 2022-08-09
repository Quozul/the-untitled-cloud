package dev.quozul.database.models

import com.github.dockerjava.api.model.ExposedPort
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.database.helpers.ApiProduct
import dev.quozul.database.helpers.ApiServer
import dev.quozul.database.helpers.DockerContainer
import dev.quozul.servers.models.ServerState
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * A container can also be called a Service.
 */

object Containers : UUIDTable("container") {
	val subscription = reference("subscription", Subscriptions)
	val product = reference("product", Products)
	val containerId = char("container_id", 64).nullable() // External foreign key to Docker
	val containerTag = varchar("container_tag", 255).default("latest")
}

class Container(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Container>(Containers)

	var subscription by Subscription referencedOn Containers.subscription
	var product by Product referencedOn Containers.product
	var containerId by Containers.containerId
	var containerTag by Containers.containerTag

	fun toApiContainer(): ApiContainer {
		val server = ApiServer.findFromContainer(this)

		val exposedPort = ExposedPort.tcp(25565)
		val port = dockerContainer?.let { dc -> dc.networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec }
		val state = ServerState.fromContainerState(dockerContainer?.state)

		return ApiContainer(
			server,
			product.toApiProduct(),
			id.toString(),
			containerTag,
			port,
			state,
		)
	}

	val dockerContainer: DockerContainer?
		get() = containerId?.let { DockerContainer(it) }
}

fun findContainerFromId(id: UUID) = transaction {
	Container.findById(id)
}

fun findWithOwner(owner: UUID, status: SubscriptionStatus = SubscriptionStatus.ACTIVE) = transaction {
	val query = Containers.innerJoin(Users)
		.innerJoin(Subscriptions)
		.slice(Containers.columns)
		.select { (Users.id eq owner) and (Subscriptions.subscriptionStatus eq status) }
		.withDistinct()

	Container.wrapRows(query)
}