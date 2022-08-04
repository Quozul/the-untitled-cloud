package dev.quozul.servers.models

import dev.quozul.servers.ServerStatus
import kotlinx.serialization.Serializable

@Serializable
data class ApiServer(
	val id: String,
	val subscriptionStatus: ServerStatus,
	val name: String?,
	val serverStatus: String?,
)

@Serializable
data class Paginate<T>(
	val data: List<T>,
	val firstPage: Boolean,
	val lastPage: Boolean,
	val totalPages: Long,
	val totalElements: Long,
)

@Serializable
data class DetailedApiServer(
	val id: String,
	val subscriptionStatus: ServerStatus,
	val serverCreated: Boolean,
	val name: String?,
	val port: String?,
	val state: ServerState?,
	val parameters: ServerParameters,
)