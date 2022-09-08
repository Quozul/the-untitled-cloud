package dev.quozul.database.models

import dev.quozul.database.enums.GameServerE
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiProduct
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Products : UUIDTable("product") {
	val name = varchar("name", 32)
	val description = varchar("description", 1024)
	val stripeId = varchar("stripe_id", 32).uniqueIndex()
	val dockerImage = varchar("docker_image", 64) // Default image of the container
	val price = integer("price").default(100)
	val cpu = integer("cpu").default(1)
	val memory = integer("memory").default(512)
	val stocks = integer("stocks").default(1)
	val gameServer = enumeration<GameServerE>("game_server").nullable()
}

class Product(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Product>(Products)

	var name by Products.name
	var description by Products.description
	var stripeId by Products.stripeId
	var dockerImage by Products.dockerImage
	var price by Products.price
	var cpu by Products.cpu
	var memory by Products.memory
	var stocks by Products.stocks
	var gameServer by Products.gameServer

	fun toApiProduct() = ApiProduct(
		id.toString(),
		name,
		description,
		isInStocks(),
		price,
		cpu,
		memory,
	)

	fun isInStocks() = usedStocks < stocks

	private val usedStocks: Long
		get() = transaction {
			val query = SubscriptionItems.innerJoin(Subscriptions)
				.slice(SubscriptionItems.product, Subscriptions.subscriptionStatus)
				.select {
					(SubscriptionItems.product eq this@Product.id) and (Subscriptions.subscriptionStatus neq SubscriptionStatus.CANCELLED)
				}
				.withDistinct()

			SubscriptionItem.wrapRows(query).count()
		}
}

fun getProductFromStripeId(stripeId: String): Product? = transaction {
	Product.find {
		Products.stripeId eq stripeId
	}.firstOrNull()
}