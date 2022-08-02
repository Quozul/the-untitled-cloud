package dev.quozul.versions

import dev.quozul.versions.models.Version
import dev.quozul.versions.models.VersionManifest
import dev.quozul.versions.models.VersionType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Route.configureVersionRoutes() {
	val versionManifest = this::class.java.classLoader.getResource("version_manifest.json")!!.readText()
	val format = Json { isLenient = true }
	val manifest = format.decodeFromString<VersionManifest>(versionManifest)

	val versions = manifest.versions.map {
		if (it.type == "snapshot" || it.type == "release") {
			return@map Version(
				it.id,
				if (it.type == "snapshot") VersionType.SNAPSHOT else VersionType.RELEASE,
			)
		} else {
			return@map null
		}
	}.filterNotNull()

	get("") {
		// TODO: Add a query param with ServerType
		// TODO: Check JDK compatibility with ServerType
		// TODO: Add an endpoint to retrieve JDK versions
		call.respond(versions)
	}
}