package dev.quozul.database.models

import com.stripe.model.Invoice
import dev.quozul.database.enums.SubscriptionProvider
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiSubscription
import dev.quozul.servers.models.Paginate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Subscriptions : UUIDTable("subscription") {
	// Foreign keys
	val owner = reference("owner", Users, onDelete = ReferenceOption.CASCADE)

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
	var containers by Container via SubscriptionItems
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

	fun toPaginatedApiProducts(page: Int, size: Int) = transaction {
		val offset = (page * size).toLong()
		val count = products.count()
		val lastPage = count <= (page + 1) * size

		Paginate(
			products.limit(size, offset).map { it.toApiProductInfo() },
			page == 0,
			lastPage,
			count / size,
			count,
			page,
		)
	}

	fun toPaginatedApiContainers(page: Int, size: Int) = transaction {
		val offset = (page * size).toLong()
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

/**
 * Get, update or create a subscription
 * Sets the products according to the invoice
 * Update the status if provided is null
 * Default status is PENDING upon creation only
 * If products weren't registered, register them
 */
fun getOrCreateSubscriptionFromInvoice(
	invoice: Invoice,
	user: User,
	status: SubscriptionStatus? = null,
	provider: SubscriptionProvider = SubscriptionProvider.STRIPE
) = transaction {
	getSubscriptionFromStripeId(invoice.subscription)?.let { subscription ->
		// There are missing products
		// TODO: Verify all products are correctly registered
		if (subscription.items.count() == 0L) {
			val products = invoice.lines.data.mapNotNull {
				// TODO: Add support for `it.quantity`
				getProductFromStripeId(it.price.id)
			}

			subscription.products = SizedCollection(products)
		}

		if (status !== null) {
			subscription.subscriptionStatus = status
		}

		subscription
	} ?: run {
		// Subscription is not created
		val products = invoice.lines.data.mapNotNull {
			getProductFromStripeId(it.price.id)
		}

		Subscription.new {
			owner = user
			stripeId = invoice.subscription
			subscriptionStatus = status ?: SubscriptionStatus.PENDING
			subscriptionProvider = provider
			this.stripeId = stripeId
			this.products = SizedCollection(products)
		}
	}
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