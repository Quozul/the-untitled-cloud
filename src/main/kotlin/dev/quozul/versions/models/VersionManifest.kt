package dev.quozul.versions.models

import kotlinx.serialization.Serializable

@Serializable
data class VersionManifest(
	val latest: VersionManifestLatest,
	val versions: List<VersionManifestVersion>,
)

@Serializable
data class VersionManifestLatest(
	val release: String,
	val snapshot: String,
)

@Serializable
data class VersionManifestVersion(
	val id: String,
	val type: String,
	val url: String,
	val time: String,
	val releaseTime: String,
)