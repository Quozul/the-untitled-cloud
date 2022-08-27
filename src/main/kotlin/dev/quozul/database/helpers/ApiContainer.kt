package dev.quozul.database.helpers

import dev.quozul.servers.models.ServerState
import kotlinx.serialization.Serializable

@Serializable
data class ApiContainer(
	val id: String?,
	val product: ApiProduct,
	val tag: String?,
	val name: String?,

	// Docker details
	val port: String?,
	val state: ServerState?,

	// Expendable fields
	val subscription: ApiSubscription,
)