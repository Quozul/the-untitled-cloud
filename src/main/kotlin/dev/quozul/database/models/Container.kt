package dev.quozul.database.models

import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.ExposedPort
import dev.quozul.database.helpers.DockerContainer
import dev.quozul.servers.models.ServerState
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * A container can also be called a Service.
 */

// TODO: This table can be auto increment
object Containers : UUIDTable("container") {
	val containerId = char("container_id", 64).nullable() // External foreign key to Docker
	val containerTag = varchar("container_tag", 255).default("latest")

	// TODO: Make name unique
	val name = varchar("name", 32).nullable() // User given name of their product
}

class Container(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Container>(Containers)

	var containerId by Containers.containerId
	var containerTag by Containers.containerTag
	var name by Containers.name

	var dockerContainer: DockerContainer?
		get() = containerId?.let { DockerContainer(it) }
		set(value) {
			containerId = value?.containerId
		}

	val port: String?
		get() {
			// TODO: Get exposed port from product configuration
			val exposedPort = ExposedPort.tcp(25565)

			return try {
				dockerContainer?.let { dc ->
					dc.networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec
				}
			} catch (_: NotFoundException) {
				null
			}
		}

	val state: ServerState
		get() = ServerState.fromContainerState(
			try {
				dockerContainer?.state
			} catch (_: NotFoundException) {
				null
			}
		)

	val server: Server?
		get() = Server.find {
			Servers.container eq id
		}.firstOrNull()
}

@Deprecated("Unsafe, use findContainerWithOwnership", replaceWith = ReplaceWith("findContainerWithOwnership()"))
fun findContainerFromId(id: UUID) = transaction {
	Container.findById(id)
}

@Deprecated("Can return null, use findItemWithOwnership", replaceWith = ReplaceWith("findItemWithOwnership()"))
fun findContainerFromItemWithOwnership(id: UUID, owner: UUID) = transaction {
	SubscriptionItem.findById(id)?.let {
		if (it.subscription.owner.id.value != owner) {
			null
		} else {
			it.container
		}
	}
}