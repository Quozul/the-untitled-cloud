package dev.quozul.database.helpers

import dev.quozul.database.models.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ApiProduct(
	val id: String,
	val name: String,
	val description: String,
) {
	companion object {
		// TODO: Get all products

		fun fromSubscription(subscription: Subscription): List<ApiProduct> = transaction {
			val query = SubscriptionItems.innerJoin(Products)
				.slice(Products.columns)
				.select { SubscriptionItems.subscription eq subscription.id }
				.withDistinct()

			Product.wrapRows(query).map {
				ApiProduct(
					it.id.toString(),
					it.name,
					it.description,
				)
			}
		}
	}
}