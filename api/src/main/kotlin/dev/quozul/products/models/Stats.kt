package dev.quozul.products.models

import kotlinx.serialization.Serializable

@Serializable
data class Stats(
	val users: Long,
	val sold: Long,
	val products: Long,
)
