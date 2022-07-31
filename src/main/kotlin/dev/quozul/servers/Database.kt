package dev.quozul.servers

import dev.quozul.authentication.User
import dev.quozul.authentication.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/*
 * dev.quozul.servers.models.Servers
 */

object Servers: UUIDTable() {
	val owner = reference("owner", Users) // Foreign key to User.id
	val subscriptionId = varchar("subscription_id", 255).uniqueIndex() // External foreign key to Stripe
	val containerId = char("container_id", 64) // External foreign key to Docker
}

class Server(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Server>(Servers)

	var owner by User referencedOn Servers.owner
	var subscriptionId by Servers.subscriptionId
	var containerId by Servers.containerId
}