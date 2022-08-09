package dev.quozul.database.models

import org.jetbrains.exposed.sql.Table

/**
 * Links a product and a subscription.
 * The combination between the 3 must be unique
 */

object SubscriptionItems : Table("subscription_item") {
	val subscription = reference("subscription", Subscriptions)
	val product = reference("product", Products)
	override val primaryKey = PrimaryKey(subscription, product)
}