package dev.quozul.servers.models

import dev.quozul.servers.Parameter
import dev.quozul.servers.Parameters
import dev.quozul.servers.Server
import dev.quozul.servers.ServerType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class ServerParameters(
	val name: String,
	val tag: String,
	val version: String,
	val eula: Boolean,
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
		fun fromServerId(id: UUID): ServerParameters {
			val (server, parameters) = transaction {
				val parameter = Parameter.find {
					Parameters.server eq id
				}.first()

				Pair(Server.findById(id)!!, parameter)
			}

			return ServerParameters(
				server.containerName,
				server.containerTag,
				parameters.version.lowercase(),
				parameters.eula,
				parameters.serverType,
				parameters.useAikar,
				parameters.jvmFlags,
				parameters.forgeVersion,
				parameters.fabricLauncherVersion,
				parameters.fabricLoaderVersion,
				parameters.quiltLauncherVersion,
				parameters.quiltLoaderVersion,
				parameters.ftbModpackId,
				parameters.ftbModpackVersionId,
			)
		}
	}
}