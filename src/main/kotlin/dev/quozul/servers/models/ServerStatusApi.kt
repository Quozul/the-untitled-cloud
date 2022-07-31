package dev.quozul.servers.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerStatusApi(
	val state: String,
)
