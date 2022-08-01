package dev.quozul.servers.models

import dev.quozul.servers.Parameter
import dev.quozul.servers.ServerType
import kotlinx.serialization.Serializable

@Serializable
data class ServerParameters(
	val version: String,
	val eula: Boolean,
	val serverType: ServerType,
	val forgeVersion: String?,
	val fabricLauncherVersion: String?,
	val fabricLoaderVersion: String?,
	val quiltLauncherVersion: String?,
	val quiltLoaderVersion: String?,
	val ftbModpackId: Int?,
	val ftbModpackVersionId: Int?,
) {
	companion object {
		fun fromParameterEntity(entity: Parameter): ServerParameters {
			return ServerParameters(
				entity.version.lowercase(),
				entity.eula,
				entity.serverType,
				entity.forgeVersion,
				entity.fabricLauncherVersion,
				entity.fabricLoaderVersion,
				entity.quiltLauncherVersion,
				entity.quiltLoaderVersion,
				entity.ftbModpackId,
				entity.ftbModpackVersionId,
			)
		}
	}
}