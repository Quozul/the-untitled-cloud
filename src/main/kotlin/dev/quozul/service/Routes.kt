package dev.quozul.service

import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.exception.NotModifiedException
import dev.quozul.authentication.hashString
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.authentication.models.PasswordCredentials
import dev.quozul.database.enums.SubscriptionStatus
import dev.quozul.database.extensions.subscription.isCancelled
import dev.quozul.database.extensions.subscription.isPending
import dev.quozul.database.extensions.subscription.isSuspended
import dev.quozul.database.extensions.subscriptionItem.*
import dev.quozul.database.helpers.ApiContainer
import dev.quozul.database.helpers.ApiContainerInfo
import dev.quozul.database.helpers.ApiServer
import dev.quozul.database.helpers.ApiServiceUpdate
import dev.quozul.database.models.*
import dev.quozul.servers.helpers.NameGenerator
import dev.quozul.servers.models.Action
import dev.quozul.servers.models.Paginate
import dev.quozul.servers.models.ServerActionRequest
import dev.quozul.servers.models.ServerState
import dev.quozul.service.models.FtpPassword
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


fun Route.configureServiceRoutes() {
	val salt = environment!!.config.property("passwords.salt").getString()
	val pepper = environment!!.config.property("passwords.pepper").getString()

	get {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val status = call.request.queryParameters["status"]?.let {
			try {
				SubscriptionStatus.valueOf(it)
			} catch (_: IllegalArgumentException) {
				SubscriptionStatus.ACTIVE
			}
		}

		val offset = (page * size).toLong()

		// Get all the services a customer has
		// If a service is not created yet, or has the PENDING status, it must be returned as well
		// TODO: Simplify this transaction
		val (services, count) = transaction {
			val query = SubscriptionItems.leftJoin(Products)
				.leftJoin(Subscriptions)
				.fullJoin(Containers)
				.slice(
					SubscriptionItems.id,
					Products.name,
					Subscriptions.id,
					Containers.name,
				).let {
					status?.let { status ->
						it.select {
							(Subscriptions.owner eq uuid) and (Subscriptions.subscriptionStatus eq status)
						}
					} ?: it.select {
						(Subscriptions.owner eq uuid) and (Subscriptions.subscriptionStatus neq SubscriptionStatus.CANCELLED)
					}
				}
				.withDistinct()

			val count = query.count()

			val response = query.limit(size, offset).map { row ->
				// This should never be null, but if it happens, I'm screwed
				val item: SubscriptionItem = SubscriptionItem.findById(row[SubscriptionItems.id])!!
				val subscription: Subscription = Subscription.findById(row[Subscriptions.id])!!

				ApiContainerInfo(
					id = row[SubscriptionItems.id].toString(),
					name = row.getOrNull(Containers.name),
					productName = row[Products.name],
					active = item.isActive,
					pending = subscription.isPending,
					cancelled = subscription.isCancelled,
					available = item.isAvailable,
					suspended = subscription.isSuspended,
				)
			}

			Pair(response, count)
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

		findItemWithOwnership(serviceId, ownerId)?.let {
			call.respond(
				transaction {
					it.toApiContainer()
				}
			)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	put("{serviceId}") {
		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val update = try {
			call.receive<ApiServiceUpdate>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		findItemWithOwnership(serviceId, ownerId)?.let {
			transaction {
				it.container?.name = update.name ?: NameGenerator.getRandomName()
				it.container?.containerTag = update.tag
			}
			call.response.status(HttpStatusCode.NoContent)
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
			findItemWithOwnership(serviceId, ownerId)?.let {
				val isActive = transaction { it.isActive }

				if (!isActive && action != Action.RECREATE) {
					call.response.status(HttpStatusCode.Forbidden)
					call.respond(AuthenticationErrors.ACTION_NOT_ALLOWED.toHashMap(true))
					return@patch
				}

				when (action) {
					Action.START -> {
						it.dockerContainer?.start()
					}

					Action.STOP -> {
						it.dockerContainer?.stop()
					}

					Action.RESTART -> {
						it.dockerContainer?.restart()
					}

					Action.RECREATE -> {
						it.recreate()
					}

					Action.RESET -> {
						it.remove()
						it.createContainer()
					}
				}
			}
			call.response.status(HttpStatusCode.NoContent)
		} catch (_: NotModifiedException) {
			call.response.status(HttpStatusCode.InternalServerError)
			call.respond(AuthenticationErrors.NOT_MODIFIED.toHashMap(true))
		} catch (e: NullPointerException) {
			e.printStackTrace()
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.NO_CONTAINER.toHashMap(true))
		}
	}

	get("{serviceId}/server") {
		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		transaction {
			val item = findItemWithOwnership(serviceId, ownerId)
			item?.container?.let { findServerFromContainer(it)?.toApiServer() }
		}?.let {
			call.respond(it)
		} ?: call.response.status(HttpStatusCode.NotFound)
	}

	put("{serviceId}/server") {
		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val parameters = try {
			call.receive<ApiServer>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@put
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		transaction {
			val item = findItemWithOwnership(serviceId, ownerId)

			item?.container?.let { findServerFromContainer(it) }?.let {
				it.version = parameters.version
				it.serverType = parameters.serverType
				it.forgeVersion = parameters.forgeVersion
				it.fabricLauncherVersion = parameters.fabricLauncherVersion
				it.fabricLoaderVersion = parameters.fabricLoaderVersion
				it.quiltLauncherVersion = parameters.quiltLauncherVersion
				it.quiltLoaderVersion = parameters.quiltLoaderVersion
				it.ftbModpackId = parameters.ftbModpackId
				it.ftbModpackVersionId = parameters.ftbModpackVersionId
				it.useAikar = parameters.useAikar
				it.jvmFlags = parameters.jvmFlags

				call.response.status(HttpStatusCode.NoContent)
			} ?: call.response.status(HttpStatusCode.NotFound)
		}
	}

	patch("{serviceId}/ftp") {
		val credentials = try {
			call.receive<PasswordCredentials>()
		} catch (e: SerializationException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val serviceId = try {
			UUID.fromString(call.parameters["serviceId"]!!)
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@patch
		}

		val principal = call.principal<JWTPrincipal>()
		val ownerId = UUID.fromString(principal!!.payload.getClaim("id").asString())

		// TODO: Find a better way to hash password
		val hash = hashString("SHA-256", salt + credentials.password + pepper)

		val user = transaction {
			User.find {
				(Users.id eq ownerId) and (Users.password eq hash)
			}.firstOrNull()
		}

		if (user == null) {
			call.response.status(HttpStatusCode.BadRequest)
			call.respond(AuthenticationErrors.INVALID_CREDENTIALS.toHashMap(true))
			return@patch
		}

		findItemWithOwnership(serviceId, ownerId)?.let {
			val password = getRandomString(32)
			transaction {
				it.ftpPassword = hashString("MD5", password)
			}
			call.respond(FtpPassword(password))
		} ?: call.response.status(HttpStatusCode.NotFound)
	}
}