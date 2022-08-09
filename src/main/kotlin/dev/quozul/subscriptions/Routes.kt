package dev.quozul.subscriptions

import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.models.*
import dev.quozul.servers.models.Paginate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.configureSubscriptionRoutes() {
	get {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val status = call.request.queryParameters["status"]
		val offset = (page * size).toLong();

		val (servers, count) = transaction {
			val servers = when (status) {
				"CANCELLED" -> {
					val query = Subscriptions.innerJoin(Users)
						.slice(Subscriptions.columns)
						.select { (Users.id eq uuid) and (Subscriptions.subscriptionStatus eq SubscriptionStatus.CANCELLED) }
						.withDistinct()

					Subscription.wrapRows(query)
				}

				"PENDING" -> {
					val query = Subscriptions.innerJoin(Users)
						.slice(Subscriptions.columns)
						.select { (Users.id eq uuid) and (Subscriptions.subscriptionStatus eq SubscriptionStatus.PENDING) }
						.withDistinct()

					Subscription.wrapRows(query)
				}

				"ACTIVE" -> {
					val query = Subscriptions.innerJoin(Users)
						.slice(Subscriptions.columns)
						.select { (Users.id eq uuid) and (Subscriptions.subscriptionStatus eq SubscriptionStatus.ACTIVE) }
						.withDistinct()

					Subscription.wrapRows(query)
				}

				else -> {
					val query = Subscriptions.innerJoin(Users)
						.slice(Subscriptions.columns)
						.select { (Users.id eq uuid) and (Subscriptions.subscriptionStatus neq SubscriptionStatus.CANCELLED) }
						.withDistinct()

					Subscription.wrapRows(query)
				}
			}

			Pair(servers.limit(size, offset).map { it.toApiSubscription() }, servers.count())
		}

		val lastPage = count <= (page + 1) * size

		val response = Paginate(
			servers,
			page == 0,
			lastPage,
			count / size,
			count,
			page,
		)

		call.respond(response)
	}

	get("{subscriptionId}") {
		val subscriptionId = try {
			UUID.fromString(call.parameters["subscriptionId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		transaction {
			Subscription.findById(subscriptionId)?.toApiSubscription()
		}?.let {
			call.respond(it)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}
}