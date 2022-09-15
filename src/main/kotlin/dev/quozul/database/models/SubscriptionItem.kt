package dev.quozul.database.models

import com.github.dockerjava.api.exception.NotFoundException
import dev.quozul.database.extensions.subscriptionItem.port
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.servers.models.ServerState
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Links a product and a subscription.
 * The combination between the 3 must be unique
 */

object SubscriptionItems : UUIDTable("subscription_item") {
	val subscription = reference("subscription", Subscriptions, onDelete = ReferenceOption.CASCADE)
	val product = reference("product", Products, onDelete = ReferenceOption.RESTRICT)
	val container = reference("container", Containers, onDelete = ReferenceOption.SET_NULL).nullable()

	init {
		index(true, subscription, product, container)
	}
}

class SubscriptionItem(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<SubscriptionItem>(SubscriptionItems)

	var subscription by Subscription referencedOn SubscriptionItems.subscription
	var product by Product referencedOn SubscriptionItems.product
	var container by Container optionalReferencedOn SubscriptionItems.container

	fun toApiContainer(): ApiContainer {
		val state: ServerState = ServerState.fromContainerState(
			try {
				container?.dockerContainer?.state
			} catch (_: NotFoundException) {
				null
			},
			transaction { container?.containerStatus }
		)

		return ApiContainer(
			id = id.toString(),
			product = product.toApiProductInfo(),
			tag = container?.containerTag,
			name = container?.name,
			port = port,
			state = state,
			subscription = subscription.toApiSubscription(),
		)
	}

}

fun findItemWithOwnership(id: UUID, owner: UUID) = transaction {
	SubscriptionItem.findById(id)?.let {
		if (it.subscription.owner.id.value != owner) {
			null
		} else {
			it
		}
	}
}