package dev.quozul.servers.models

import kotlinx.serialization.Serializable


enum class Action {
	START,
	STOP,
	RESTART,
}


@Serializable
data class ServerActionRequest(
	val action: Action,
);
