package dev.quozul.servers.models

import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState
import dev.quozul.database.enums.ContainerStatus
import kotlinx.serialization.Serializable

enum class ServerStatus {
	// Docker statuses
	RESTARTING,
	RUNNING,

	// Custom statuses
	PENDING,
	UNAVAILABLE,
	STARTING,
	STOPPED,
}

@Serializable
data class ServerState(
	val status: ServerStatus,
	val pending: Boolean,
	val created: Boolean,
	val running: Boolean,
	val starting: Boolean,
	val startedAt: String?,
	val finishedAt: String?,
) {
	companion object {
		fun fromContainerState(containerState: ContainerState?, containerStatus: ContainerStatus?): ServerState {
			val status = if (containerState == null) {
				if (containerStatus === ContainerStatus.DOWNLOADING || containerStatus === ContainerStatus.CREATING) {
					ServerStatus.PENDING
				} else {
					ServerStatus.UNAVAILABLE
				}
			} else if (containerState.health?.status == "starting") {
				ServerStatus.STARTING
			} else if (containerState.restarting == true) {
				ServerStatus.RESTARTING
			} else if (containerState.running == true) {
				ServerStatus.RUNNING
			} else {
				ServerStatus.STOPPED
			}

			return ServerState(
				status,
				containerStatus === ContainerStatus.DOWNLOADING || containerStatus === ContainerStatus.CREATING,
				containerState != null,
				containerState?.running == true,
				containerState?.health?.status == "starting",
				containerState?.startedAt, // TODO: Replace startedAt and finishedAt with null
				containerState?.finishedAt,
			)
		}
	}
}