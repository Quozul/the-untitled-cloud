package dev.quozul.servers.models

import com.github.dockerjava.api.exception.NotFoundException
import dev.quozul.dockerClient
import dev.quozul.servers.Server
import dev.quozul.servers.ServerStatus
import kotlinx.serialization.Serializable

@Serializable
data class ApiServer(
	val id: String,
	val subscriptionStatus: ServerStatus,
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

			return ApiServer(server.id.toString(), server.status, container?.name, container?.state?.status)
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
	val subscriptionStatus: ServerStatus,
	val serverCreated: Boolean,
	val name: String?,
	val port: String?,
	val state: ServerState?,
	val parameters: ServerParameters,
)