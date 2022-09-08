package dev.quozul.database.models

import dev.quozul.database.enums.ServerType
import dev.quozul.database.helpers.ApiServer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Servers : UUIDTable("server") {
	val container = reference("container", Containers, onDelete = ReferenceOption.CASCADE)

	val version = Servers.varchar("version", 8).default("LATEST")
	val serverType = Servers.enumeration<ServerType>("server_type").default(ServerType.VANILLA)
	val useAikar = Servers.bool("use_aikar").default(true)
	val jvmFlags = Servers.varchar("jvm_flags", 1024).nullable()

	// Type specific Servers

	// Forge
	val forgeVersion = Servers.varchar("forgeversion", 16).nullable()

	// Fabric
	val fabricLauncherVersion = Servers.varchar("fabric_launcher_version", 16).nullable()
	val fabricLoaderVersion = Servers.varchar("fabric_loader_version", 16).nullable()

	// Quilt
	val quiltLauncherVersion = Servers.varchar("quilt_launcher_version", 16).nullable()
	val quiltLoaderVersion = Servers.varchar("quilt_loader_version", 16).nullable()

	// FTB
	val ftbModpackId = Servers.integer("ftb_modpack_id").nullable()
	val ftbModpackVersionId = Servers.integer("ftb_modpack_version_id").nullable()
}

class Server(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Server>(Servers)

	var container by Container referencedOn Servers.container

	var version by Servers.version
	var serverType by Servers.serverType
	var useAikar by Servers.useAikar
	var jvmFlags by Servers.jvmFlags

	var forgeVersion by Servers.forgeVersion
	var fabricLauncherVersion by Servers.fabricLauncherVersion
	var fabricLoaderVersion by Servers.fabricLoaderVersion
	var quiltLauncherVersion by Servers.quiltLauncherVersion
	var quiltLoaderVersion by Servers.quiltLoaderVersion
	var ftbModpackId by Servers.ftbModpackId
	var ftbModpackVersionId by Servers.ftbModpackVersionId

	fun toEnvironmentVariables(): List<String> {
		// TODO: Use env from GameServer enum
		val list = mutableListOf(
			"VERSION=${version}",
			"EULA=TRUE",
			"TYPE=${serverType}",
			"MOTD=Hosted by theuntitledcloud.com",
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

	fun toApiServer() = ApiServer(
		version.lowercase(),
		serverType,
		useAikar,
		jvmFlags,
		forgeVersion,
		fabricLauncherVersion,
		fabricLoaderVersion,
		quiltLauncherVersion,
		quiltLoaderVersion,
		ftbModpackId,
		ftbModpackVersionId,
	)
}

fun findServerFromContainer(container: Container): Server? = transaction {
	Server.find {
		Servers.container eq container.id
	}.firstOrNull()
}