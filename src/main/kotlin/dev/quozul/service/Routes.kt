package dev.quozul.service

import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.ExposedPort
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.database.helpers.DockerContainer
import dev.quozul.database.models.*
import dev.quozul.servers.models.Action
import dev.quozul.servers.models.Paginate
import dev.quozul.servers.models.ServerActionRequest
import dev.quozul.servers.models.ServerState
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Route.configureServiceRoutes() {
	get {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val offset = (page * size).toLong();

		// TODO: Add filter

		// Get all the services a customer has
		// If a service is not created yet, or has the PENDING status, it must be returned as well
		val (services, count) = transaction {
			val query = SubscriptionItems.innerJoin(Products)
				.innerJoin(Subscriptions)
				.leftJoin(Containers)
				.slice(
					Containers.id,
					Products.id,
					Subscriptions.id,
					Subscriptions.subscriptionStatus,
					Containers.containerTag,
					Containers.containerId,
					Subscriptions.name
				)
				.select { Subscriptions.owner eq uuid }
				.withDistinct()

			val response = query.limit(size, offset).map { row ->
				val dockerContainer: DockerContainer? = row.getOrNull(Containers.containerId)?.let { DockerContainer(it) }
				val product: Product = Product.findById(row[Products.id])!! // This should never be null, but if it happens, I'm screwed
				val subscription: Subscription = Subscription.findById(row[Subscriptions.id])!!

				val exposedPort = ExposedPort.tcp(25565)
				val port = dockerContainer?.let { dc -> dc.networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec }
				val state = ServerState.fromContainerState(dockerContainer?.state)

				ApiContainer(
					id = row.getOrNull(Containers.id)?.toString(),
					product = product.toApiProduct(),
					tag = row.getOrNull(Containers.containerTag),
					port,
					state,
					subscription.toApiSubscription(),
				)
			}

			Pair(response, query.count())
		}

		val lastPage = count <= (page + 1) * size

		val response = Paginate(
			services,
			page == 0,
			lastPage,
			count / size,
			count,
			page,
		)

		call.respond(response)
	}

	get("{serviceId}") {
		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findContainerWithOwnership(serviceId, ownerId)?.let {
			call.respond(
				transaction {
					it.toApiContainer()
				}
			)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	patch("{serviceId}") {
		val action = try {
			call.receive<ServerActionRequest>().action
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		try {
			findContainerWithOwnership(serviceId, ownerId)?.dockerContainer?.let {
				when (action) {
					Action.START -> {
						it.start()
					}

					Action.STOP -> {
						it.stop()
					}

					Action.RESTART -> {
						it.restart()
					}

					Action.RECREATE -> {
						it.recreate()
					}

					Action.RESET -> {
						it.reset()
					}
				}
				call.response.status(HttpStatusCode.NoContent)
			}
		} catch (_: NotModifiedException) {
			call.response.status(HttpStatusCode.InternalServerError)
			call.respond(AuthenticationErrors.NOT_MODIFIED.toHashMap(true))
		} catch (_: NullPointerException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
		}
	}
}