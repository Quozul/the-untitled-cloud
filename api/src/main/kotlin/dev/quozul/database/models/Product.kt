package dev.quozul.database.models

import dev.quozul.database.enums.GameServerE
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiProduct
import dev.quozul.database.helpers.ApiProductInfo
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Products : IdTable<GameServerE>("product") {
	override val id: Column<EntityID<GameServerE>> = enumeration<GameServerE>("id").entityId()
	override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }

	val name = varchar("name", 32)
	val description = varchar("description", 1024)
	val stripeId = varchar("stripe_id", 32).uniqueIndex()
	val dockerImage = varchar("docker_image", 64) // Default image of the container
	val price = integer("price").default(100)
	val cpu = integer("cpu").default(1)
	val memory = integer("memory").default(512)
	val stocks = integer("stocks").default(1)
}

class Product(id: EntityID<GameServerE>) : Entity<GameServerE>(id) {
	companion object : EntityClass<GameServerE, Product>(Products)

	var name by Products.name
	var description by Products.description
	var stripeId by Products.stripeId
	var dockerImage by Products.dockerImage
	var price by Products.price
	var cpu by Products.cpu
	var memory by Products.memory
	var stocks by Products.stocks

	fun toApiProduct() = ApiProduct(
		id.toString(),
		name,
		description,
		inStock,
		price,
		cpu,
		memory,
	)

	fun toApiProductInfo() = ApiProductInfo(
		id.toString(),
		name,
		description,
	)

	val gameServer: GameServerE
		get() = id.value

	private val inStock: Boolean
		get() = usedStocks < stocks

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