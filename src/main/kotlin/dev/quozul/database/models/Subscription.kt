package dev.quozul.database.models

import dev.quozul.database.enums.SubscriptionProvider
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.database.helpers.ApiProduct
import dev.quozul.database.helpers.ApiSubscription
import dev.quozul.servers.models.Paginate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Subscriptions : UUIDTable("subscription") {
	// Foreign keys
	val owner = reference("owner", Users)

	val subscriptionStatus = enumeration<SubscriptionStatus>("status")
		.default(SubscriptionStatus.REGISTERED) // Status of the subscription
	val subscriptionProvider = enumeration<SubscriptionProvider>("provider")
		.default(SubscriptionProvider.STRIPE)
	val stripeId = char("stripe_id", 64).uniqueIndex().nullable()
	val creationDate = datetime("creationDate").defaultExpression(CurrentDateTime())
	val deletionDate = datetime("deletionDate").nullable()
}

class Subscription(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Subscription>(Subscriptions)

	var owner by User referencedOn Subscriptions.owner

	var subscriptionStatus by Subscriptions.subscriptionStatus
	var subscriptionProvider by Subscriptions.subscriptionProvider
	var stripeId by Subscriptions.stripeId
	var creationDate by Subscriptions.creationDate
	var deletionDate by Subscriptions.deletionDate

	var products by Product via SubscriptionItems
	val containers by Container referrersOn Containers.subscription
	val items by SubscriptionItem referrersOn SubscriptionItems.subscription

	fun toApiSubscription(): ApiSubscription {
		return ApiSubscription(
			id.toString(),
			subscriptionStatus,
			subscriptionProvider,
			LocalDateTime.parse(creationDate.toString()),
			deletionDate?.let { LocalDateTime.parse(it.toString()) },
		)
	}

	fun toApiProducts() = transaction {
		products.map { it.toApiProduct() }
	}

	fun toApiContainers() = transaction {
		items.map { it.toApiContainer() }
	}

	fun toPaginatedApiProducts(page: Int, size: Int) = transaction {
		val offset = (page * size).toLong();
		val count = products.count()
		val lastPage = count <= (page + 1) * size

		Paginate(
			products.limit(size, offset).map { it.toApiProduct() },
			page == 0,
			lastPage,
			count / size,
			count,
			page,
		)
	}

	fun toPaginatedApiContainers(page: Int, size: Int) = transaction {
		val offset = (page * size).toLong();
		val count = containers.count()
		val lastPage = count <= (page + 1) * size

		Paginate(
			items.limit(size, offset).map { it.toApiContainer() },
			page == 0,
			lastPage,
			count / size,
			count,
			page,
		)
	}
}

fun getSubscriptionFromStripeId(stripeId: String) = transaction {
	Subscription.find {
		Subscriptions.stripeId eq stripeId
	}.firstOrNull()
}

@Deprecated("Unsafe, use findSubscriptionWithOwnership", replaceWith = ReplaceWith("findSubscriptionWithOwnership()"))
fun getSubscriptionFromId(id: UUID) = transaction {
	Subscription.findById(id)
}

fun findSubscriptionWithOwnership(id: UUID, owner: UUID) = transaction {
	Subscription.findById(id)?.let {
		if (it.owner.id.value != owner) {
			null
		} else {
			it
		}
	}
}