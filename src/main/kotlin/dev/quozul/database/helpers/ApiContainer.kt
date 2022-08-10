package dev.quozul.database.helpers

import dev.quozul.database.models.Container
import dev.quozul.database.models.Containers
import dev.quozul.database.models.Subscription
import dev.quozul.servers.models.ServerState
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ApiContainer(
	val id: String,
	val product: String,
	val subscription: String,
	val tag: String,

	// Docker details
	val port: String?,
	val state: ServerState?,

	// Expandable fields
	val server: ApiServer? = null,
) {
	companion object {
		fun fromContainerId(containerId: UUID) = transaction {
			Container.findById(containerId)
		}

		fun fromSubscription(subscription: Subscription) = transaction {
			Container.find {
				Containers.subscription eq subscription.id
			}
				.map {
					it.toApiContainer()
				}
		}
	}
}