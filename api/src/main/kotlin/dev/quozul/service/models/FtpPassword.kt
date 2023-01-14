package dev.quozul.service.models

import kotlinx.serialization.Serializable

@Serializable
data class FtpPassword(
	val ftpPassword: String,
)
