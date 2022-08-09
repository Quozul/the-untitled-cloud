package dev.quozul.servers.models

import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.Container
import dev.quozul.servers.helpers.NameGenerator
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

@Deprecated("Use ApiContainer instead")
@Serializable
data class V1ApiServer(
	val id: String,
	val subscriptionStatus: SubscriptionStatus,
	val name: String?,
	val serverStatus: String?,
) {
	companion object {
		@Deprecated("Use Container.toApiContainer() instead")
		fun fromContainer(container: Container, status: SubscriptionStatus): V1ApiServer {
			val name = container.subscription.name

			// Generate a random name if the servers does not have one
			// TODO: This should not be happening here
			if (name == null) {
				transaction {
					container.subscription.name = NameGenerator.getRandomName()
				}
			}

			return V1ApiServer(
				container.id.toString(),
				status,
				name,
				container.dockerContainer?.state?.status
			)
		}
	}
}
