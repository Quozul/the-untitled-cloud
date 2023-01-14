package dev.quozul.database.models

import org.jetbrains.exposed.sql.Table

/**
 * Links two products to create an option.
 */

object Options : Table("option") {
	val product = reference("product", Products)
	val option = reference("option", Products)
	override val primaryKey = PrimaryKey(product)
}