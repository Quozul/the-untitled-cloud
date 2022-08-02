package dev.quozul.servers.models

import kotlinx.serialization.Serializable


enum class Action {
	START,
	STOP,
	RESTART,
	RECREATE,
}


@Serializable
data class ServerActionRequest(
	val action: Action,
);
