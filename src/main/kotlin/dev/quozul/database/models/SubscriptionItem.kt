package dev.quozul.database.models

import com.github.dockerjava.api.exception.NotFoundException
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.database.helpers.DockerContainer
import dev.quozul.servers.models.ServerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Links a product and a subscription.
 * The combination between the 3 must be unique
 */

object SubscriptionItems : UUIDTable("subscription_item") {
	val subscription = reference("subscription", Subscriptions)
	val product = reference("product", Products)
	val container = reference("container", Containers).nullable()

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
			}
		)

		return ApiContainer(
			id = id.toString(),
			product = product.toApiProduct(),
			tag = container?.containerTag,
			name = container?.name,
			port = container?.port,
			state = state,
			subscription = subscription.toApiSubscription(),
		)
	}

	suspend fun createContainer() {
		println("CREATE")
		runBlocking {
			newSuspendedTransaction(Dispatchers.Default) {
				val container = Container.new {
					this.product = this@SubscriptionItem.product
					this.subscription = this@SubscriptionItem.subscription
				}

				val server = Server.new {
					this.container = container
				}

				container.dockerContainer = DockerContainer.new(
					image = product.dockerImage,
					tag = container.containerTag,
					env = server.toEnvironmentVariables(),
				)

				this@SubscriptionItem.container = container
			}
		}
	}

	var dockerContainer: DockerContainer?
		get() = transaction { container?.dockerContainer }
		set(value) = transaction {
			container?.dockerContainer = value
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