package dev.quozul.database.models

import dev.quozul.database.helpers.ApiProduct
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Products: UUIDTable("product") {
	val name = varchar("name", 32)
	val description = varchar("description", 1024)
	val stripeId = varchar("stripe_id", 32)
	val dockerImage = varchar("docker_image", 64) // Default image of the container
	// TODO: Add default containers' limits?
}

class Product(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Product>(Products)

	var name by Products.name
	var description by Products.description
	var stripeId by Products.stripeId
	var dockerImage by Products.dockerImage

	fun toApiProduct(): ApiProduct {
		return ApiProduct(
			id.toString(),
			name,
			description,
		)
	}
}

fun getProductFromStripeId(stripeId: String): Product? {
	return transaction {
		Product.find {
			Products.stripeId eq stripeId
		}.firstOrNull()
	}
}