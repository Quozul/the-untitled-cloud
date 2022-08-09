package dev.quozul.database.helpers

import dev.quozul.database.enums.ServerType
import dev.quozul.database.models.Container
import dev.quozul.database.models.Server
import dev.quozul.database.models.Servers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ApiServer(
	val version: String,
	val serverType: ServerType,
	val useAikar: Boolean,
	val jvmFlags: String?,
	val forgeVersion: String?,
	val fabricLauncherVersion: String?,
	val fabricLoaderVersion: String?,
	val quiltLauncherVersion: String?,
	val quiltLoaderVersion: String?,
	val ftbModpackId: Int?,
	val ftbModpackVersionId: Int?,
) {
	companion object {
		fun findFromContainerId(containerId: UUID) = transaction {
			Server.find {
				Servers.container eq containerId
			}.firstOrNull()
		}?.toApiServer()

		fun findFromContainer(container: Container) = transaction {
			Server.find {
				Servers.container eq container.id
			}.firstOrNull()
		}?.toApiServer()
	}
}