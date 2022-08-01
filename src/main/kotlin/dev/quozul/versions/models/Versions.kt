package dev.quozul.versions.models

import kotlinx.serialization.Serializable

enum class VersionType {
	RELEASE,
	SNAPSHOT,
}

@Serializable
data class Version(
	val id: String,
	val type: VersionType,
)
