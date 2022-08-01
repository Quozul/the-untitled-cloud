package dev.quozul.servers.models

import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState
import kotlinx.serialization.Serializable

@Serializable
data class ServerState(
	val status: String?,
	val running: Boolean?,
	val paused: Boolean?,
	val restarting: Boolean?,
	val oomKilled: Boolean?,
	val dead: Boolean?,
	val exitCode: Long?,
	val error: String?,
	val startedAt: String?,
	val finishedAt: String?,
	val failingStreak: Int?,
) {
	companion object {
		fun fromContainerState(containerState: ContainerState): ServerState {
			return ServerState(
				containerState.status,
				containerState.running,
				containerState.paused,
				containerState.restarting,
				containerState.oomKilled,
				containerState.dead,
				containerState.exitCodeLong,
				containerState.error,
				containerState.startedAt,
				containerState.finishedAt,
				containerState.health?.failingStreak,
			)
		}
	}
}