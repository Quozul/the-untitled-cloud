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
	val id: String?,
	val product: ApiProduct,
	val tag: String?,
	val name: String?,

	// Docker details
	val port: String?,
	val state: ServerState?,

	// Expendable fields
	val subscription: ApiSubscription,
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