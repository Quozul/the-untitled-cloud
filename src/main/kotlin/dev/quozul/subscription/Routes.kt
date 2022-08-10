package dev.quozul.subscription

import com.stripe.exception.StripeException
import com.stripe.param.SubscriptionCancelParams
import com.stripe.param.SubscriptionRetrieveParams
import com.stripe.param.SubscriptionUpdateParams
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.helpers.ApiSubscription
import dev.quozul.database.models.*
import dev.quozul.servers.models.CancelMode
import dev.quozul.servers.models.Paginate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
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
		val offset = (page * size).toLong();
		val status = call.request.queryParameters["status"]

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

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findSubscriptionWithOwnership(subscriptionId, ownerId)?.let {
			call.respond(it.toApiSubscription())
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	get("{subscriptionId}/details") {
		val subscriptionId = try {
			UUID.fromString(call.parameters["subscriptionId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findSubscriptionWithOwnership(subscriptionId, ownerId)?.let {
			ApiSubscription.getDetails(it)
		}?.let {
			call.respond(it)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	get("{subscriptionId}/details/invoices") {
		// TODO
		call.response.status(HttpStatusCode.NotImplemented)
	}

	get("{subscriptionId}/services") {
		val subscriptionId = try {
			UUID.fromString(call.parameters["subscriptionId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findSubscriptionWithOwnership(subscriptionId, ownerId)?.let {
			call.respond(it.toPaginatedApiContainers(page, size))
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	get("{subscriptionId}/products") {
		val subscriptionId = try {
			UUID.fromString(call.parameters["subscriptionId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findSubscriptionWithOwnership(subscriptionId, ownerId)?.let {
			call.respond(it.toPaginatedApiProducts(page, size))
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	delete("{subscriptionId}") {
		val server = try {
			val serverId = UUID.fromString(call.parameters["serverId"]!!)
			transaction {
				Container.findById(serverId)!!
			}
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@delete
		}

		val mode = try {
			call.receive<CancelMode>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@delete
		}

		val subscription = try {
			val params = SubscriptionRetrieveParams.builder()
				.addExpand("latest_invoice")
				.build()

			com.stripe.model.Subscription.retrieve(server.subscription.stripeId, params, null)
		} catch (_: StripeException) {
			call.response.status(HttpStatusCode.NotFound)
			return@delete
		}

		if (subscription.latestInvoiceObject.paid) {
			call.response.status(HttpStatusCode.BadRequest)
			call.respond(AuthenticationErrors.NOT_PAID.toHashMap(true))
			return@delete
		}

		try {
			if (mode.now) {
				val params = SubscriptionCancelParams.builder()
					.setProrate(false) // TODO: Verify the 14 days trial period, if that's the case, do not delete the invoice
					.build()

				// Delete invoice if it hasn't been paid
				if (!subscription.latestInvoiceObject.paid) {
					subscription.latestInvoiceObject.voidInvoice()
				}

				subscription.cancel(params)
			} else {
				val params = SubscriptionUpdateParams.builder()
					.setCancelAtPeriodEnd(true)
					.build()

				subscription.update(params)
			}

			transaction {
				server.subscription.subscriptionStatus = SubscriptionStatus.SUSPENDED
			}

			call.response.status(HttpStatusCode.NoContent)
		} catch (e: StripeException) {
			e.printStackTrace()
			call.response.status(HttpStatusCode.InternalServerError)
			call.respond(AuthenticationErrors.CANCEL_ERROR.toHashMap(true))
			return@delete
		}
	}

	post {
		// TODO: Create a new subscription, return the clientSecret
		call.response.status(HttpStatusCode.NotImplemented)
	}

	put("{subscriptionId}") {
		// TODO: Update a subscription with new products
		call.response.status(HttpStatusCode.NotImplemented)
	}
}