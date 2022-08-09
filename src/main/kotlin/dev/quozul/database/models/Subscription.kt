package dev.quozul.database.models

import dev.quozul.database.enums.SubscriptionProvider
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.database.helpers.ApiSubscription
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

	// TODO: Make name unique
	val name = varchar("name", 32).nullable() // User given name of their product
}

class Subscription(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Subscription>(Subscriptions)

	var owner by User referencedOn Subscriptions.owner

	var subscriptionStatus by Subscriptions.subscriptionStatus
	var subscriptionProvider by Subscriptions.subscriptionProvider
	var stripeId by Subscriptions.stripeId
	var creationDate by Subscriptions.creationDate
	var deletionDate by Subscriptions.deletionDate
	var name by Subscriptions.name

	var products by Product via SubscriptionItems
	val containers by Container referrersOn Containers.subscription

	fun toApiSubscription(): ApiSubscription {
		return ApiSubscription(
			id.toString(),
			subscriptionStatus,
			subscriptionProvider,
			LocalDateTime.parse(creationDate.toString()),
			deletionDate?.let { LocalDateTime.parse(it.toString()) },
			name,
			products.map { it.toApiProduct() },
			containers.map { it.toApiContainer() },
		)
	}
}

fun getSubscriptionFromStripeId(stripeId: String) = transaction {
	Subscription.find {
		Subscriptions.stripeId eq stripeId
	}.firstOrNull()
}

fun getSubscriptionFromId(id: UUID) = transaction {
	Subscription.findById(id)
}