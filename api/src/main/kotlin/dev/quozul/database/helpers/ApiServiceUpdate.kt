package dev.quozul.database.helpers

import kotlinx.serialization.Serializable

@Serializable
data class ApiServiceUpdate(
	val name: String?,
	val tag: String,
)
