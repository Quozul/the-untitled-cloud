package dev.quozul.servers

import dev.quozul.authentication.User
import dev.quozul.authentication.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

/*
 * Servers
 */

enum class ServerStatus {
	PENDING, // Awaiting payment
	ACTIVE, // Payment has been made, the service can be used
	SUSPENDED, // Payment failed, but subscription isn't cancelled
	ENDED, // Customer has stopped the subscription
}

object Servers : UUIDTable("servers") {
	val owner = reference("owner", Users) // Foreign key to User.id
	val subscriptionId = varchar("subscription_id", 255).uniqueIndex() // External foreign key to Stripe
	val containerId = char("container_id", 64).nullable() // External foreign key to Docker
	val status = enumeration<ServerStatus>("status") // Status of the subscription
	val containerTag = varchar("container_tag", 255).default("latest") // Not used yet
	val containerName = varchar("container_name", 255).nullable() // Not used yet
	val creationDate = datetime("creationDate").defaultExpression(CurrentDateTime())
	val deletionDate = datetime("deletionDate").nullable()
}

class Server(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Server>(Servers)

	var owner by User referencedOn Servers.owner
	var subscriptionId by Servers.subscriptionId
	var containerId by Servers.containerId
	var status by Servers.status
	var containerTag by Servers.containerTag
	var containerName by Servers.containerName
	val creationDate by Servers.creationDate
	var deletionDate by Servers.deletionDate
	// TODO: Add tag column for JDK version
	// TODO: Add server name column, with a default random one (generated like Docker does)
}

/*
 * Parameters
 */

enum class ServerType {
	VANILLA,
	FORGE,
	FABRIC,
	QUILT,
	BUKKIT,
	SPIGOT,
	PAPER,
	PUFFERFISH,
	PURPUR,
	MAGMA,
	MOHIST,
	CATSERVER,
	LOLISERVER,
	CANYON,
	SPONGE_VANILLA,
	LIMBO,
	CRUCIBLE,
	FTBA,
	CURSEFORGE,
}

object Parameters : IntIdTable("parameters") {
	val server = Parameters.reference("server", Servers) // Foreign key to Servers.id

	val version = varchar("version", 8).default("LATEST")
	val eula = bool("eula").default(false)
	val serverType = enumeration<ServerType>("server_type").default(ServerType.VANILLA)

	// Type specific parameters

	// Forge
	val forgeVersion = varchar("forgeversion", 16).nullable()

	// Fabric
	val fabricLauncherVersion = varchar("fabric_launcher_version", 16).nullable()
	val fabricLoaderVersion = varchar("fabric_loader_version", 16).nullable()

	// Quilt
	val quiltLauncherVersion = varchar("quilt_launcher_version", 16).nullable()
	val quiltLoaderVersion = varchar("quilt_loader_version", 16).nullable()

	// FTB
	val ftbModpackId = integer("ftb_modpack_id").nullable()
	val ftbModpackVersionId = integer("ftb_modpack_version_id").nullable()
}

class Parameter(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Parameter>(Parameters)

	var server by Server referencedOn Parameters.server

	var version by Parameters.version
	var eula by Parameters.eula
	var serverType by Parameters.serverType

	var forgeVersion by Parameters.forgeVersion
	var fabricLauncherVersion by Parameters.fabricLauncherVersion
	var fabricLoaderVersion by Parameters.fabricLoaderVersion
	var quiltLauncherVersion by Parameters.quiltLauncherVersion
	var quiltLoaderVersion by Parameters.quiltLoaderVersion
	var ftbModpackId by Parameters.ftbModpackId
	var ftbModpackVersionId by Parameters.ftbModpackVersionId

	fun toEnvironmentVariables(): List<String> {
		val list = mutableListOf(
			"VERSION=${version}",
			"EULA=${eula}",
			"TYPE=${serverType}",
		)

		forgeVersion?.let { list.add("FORGEVERSION=$forgeVersion") }
		fabricLauncherVersion?.let { list.add("FABRIC_LAUNCHER_VERSION=$fabricLauncherVersion") }
		fabricLoaderVersion?.let { list.add("FABRIC_LOADER_VERSION=$fabricLoaderVersion") }
		quiltLauncherVersion?.let { list.add("QUILT_LAUNCHER_VERSION=$quiltLauncherVersion") }
		quiltLoaderVersion?.let { list.add("QUILT_LOADER_VERSION=$quiltLoaderVersion") }
		ftbModpackId?.let { list.add("FTB_MODPACK_ID=$ftbModpackId") }
		ftbModpackVersionId?.let { list.add("FTB_MODPACK_VERSION_ID=$ftbModpackVersionId") }

		return list
	}
}