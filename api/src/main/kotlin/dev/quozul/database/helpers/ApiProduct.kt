package dev.quozul.database.helpers

import kotlinx.serialization.Serializable

@Serializable
data class ApiProduct(
	val id: String,
	val name: String,
	val description: String,
	val inStocks: Boolean,
	val price: Int,
	val cpu: Int,
	val memory: Int,
)

@Serializable
data class ApiProductInfo(
	val id: String,
	val name: String,
	val description: String,
)