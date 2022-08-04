package dev.quozul.servers.models

import kotlinx.serialization.Serializable

@Serializable
data class CancelMode(val now: Boolean)
