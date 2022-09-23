package dev.quozul.database.helpers

import dev.quozul.servers.models.ServerState
import kotlinx.serialization.Serializable

@Serializable
data class ApiContainer(
	val id: String,
	val product: ApiProductInfo,
	val tag: String?,
	val name: String?,
	val hasFtpPassword: Boolean,

	// Docker details
	val port: String?,
	val state: ServerState?,

	// Expendable fields
	val subscription: ApiSubscription,
)

@Serializable
data class ApiContainerInfo(
	val id: String,
	val name: String?,
	val productName: String,
	val active: Boolean,
	val pending: Boolean,
	val cancelled: Boolean,
	val suspended: Boolean,
	val available: Boolean,
)