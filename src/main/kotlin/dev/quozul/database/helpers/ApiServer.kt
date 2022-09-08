package dev.quozul.database.helpers

import dev.quozul.database.enums.ServerType
import kotlinx.serialization.Serializable

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
)