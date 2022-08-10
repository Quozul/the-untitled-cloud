package dev.quozul.servers.models

import dev.quozul.database.enums.SubscriptionStatus
import kotlinx.serialization.Serializable

@Deprecated("Use Container.toApiContainer() instead")
@Serializable
data class V1DetailedApiServer(
	val id: String?,
	val subscriptionStatus: SubscriptionStatus,
	val name: String?,
	val port: String?,
	val state: ServerState,
)
