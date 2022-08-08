package dev.quozul.servers.models

import com.github.dockerjava.api.exception.NotFoundException
import dev.quozul.dockerClient
import dev.quozul.servers.Server
import dev.quozul.servers.SubscriptionServerStatus
import dev.quozul.servers.helpers.NameGenerator
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ApiServer(
	val id: String,
	val subscriptionStatus: SubscriptionServerStatus,
	val name: String?,
	val serverStatus: String?,
) {
	companion object {
		fun fromServer(server: Server): ApiServer {
			val container = try {
				dockerClient.inspectContainerCmd(server.containerId!!).exec()
			} catch (_: NotFoundException) {
				null
			} catch (_: NullPointerException) {
				null
			}

			// TODO: This should not be happening here
			// Generate a random name if the servers does not have one
			if (server.containerName == null) {
				transaction {
					server.containerName = NameGenerator.getRandomName()
				}
			}

			return ApiServer(server.id.toString(), server.subscriptionStatus, server.containerName, container?.state?.status)
		}
	}
}

@Serializable
data class Paginate<T>(
	val data: List<T>,
	val firstPage: Boolean,
	val lastPage: Boolean,
	val totalPages: Long,
	val totalElements: Long,
	val page: Int,
)

@Serializable
data class DetailedApiServer(
	val id: String,
	val subscriptionStatus: SubscriptionServerStatus,
	val name: String?,
	val port: String?,
	val state: ServerState,
)