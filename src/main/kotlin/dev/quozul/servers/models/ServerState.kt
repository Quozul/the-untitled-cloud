package dev.quozul.servers.models

import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState
import kotlinx.serialization.Serializable

enum class ServerStatus {
	// Docker statuses
	RESTARTING,
	RUNNING,

	// Custom statuses
	UNAVAILABLE,
	STARTING,
	STOPPED,
}

@Serializable
data class ServerState(
	val status: ServerStatus,
	val created: Boolean,
	val running: Boolean,
	val starting: Boolean,
	val startedAt: String?,
	val finishedAt: String?,
) {
	companion object {
		fun fromContainerState(containerState: ContainerState?): ServerState {
			val status = if (containerState == null) {
				ServerStatus.UNAVAILABLE
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
				containerState != null,
				containerState?.running == true,
				containerState?.health?.status == "starting",
				containerState?.startedAt, // TODO: Replace startedAt and finishedAt with null
				containerState?.finishedAt,
			)
		}
	}
}