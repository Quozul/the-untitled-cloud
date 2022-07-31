package dev.quozul.servers.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiServer(
	val id: String,
	val status: String,
	val name: String,
)

@Serializable
data class Paginate<T>(
	val data: List<T>,
	val firstPage: Boolean,
	val lastPage: Boolean,
	val totalPages: Long,
	val totalElements: Long,
)