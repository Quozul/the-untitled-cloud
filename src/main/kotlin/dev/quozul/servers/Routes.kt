package dev.quozul.servers

import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.ExposedPort
import com.stripe.exception.StripeException
import com.stripe.model.Invoice
import com.stripe.model.PaymentMethod
import com.stripe.model.Subscription
import com.stripe.param.SubscriptionCancelParams
import com.stripe.param.SubscriptionUpdateParams
import dev.quozul.authentication.models.AuthenticationError
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.dockerClient
import dev.quozul.servers.models.*
import dev.quozul.servers.models.Action.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


fun Route.configureServerRoutes() {
	get("") {
		// Get the user from the JWT
		val principal = call.principal<JWTPrincipal>()
		val uuid = UUID.fromString(principal!!.payload.getClaim("id").asString())
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val status = call.request.queryParameters["status"]

		val servers = transaction {
			val servers = when (status) {
				"ENDED" -> {
					Server.find {
						(Servers.owner eq uuid) and (Servers.status eq ServerStatus.ENDED)
					}
						.limit(size, (page * size).toLong())
				}

				else -> {
					Server.find {
						(Servers.owner eq uuid) and (Servers.status neq ServerStatus.ENDED)
					}
						.limit(size, (page * size).toLong())
				}
			}

			servers.map {
				val container = try {
					dockerClient.inspectContainerCmd(it.containerId!!).exec()
				} catch (_: NotFoundException) {
					null
				} catch (_: NullPointerException) {
					null
				}

				ApiServer(it.id.toString(), it.status, container?.name, container?.state?.status)
			}
		}

		val count = transaction {
			Server.count()
		}

		val lastPage = count <= (page + 1) * size

		val response = Paginate(
			servers,
			page == 0,
			lastPage,
			count / size,
			count,
		)

		call.respond(response)
	}

	route("{serverId}") {
		get {
			val serverId = try {
				UUID.fromString(call.parameters["serverId"]!!)
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@get
			}

			val server = try {
				transaction {
					Server.findById(serverId)!!
				}
			} catch (_: NullPointerException) {
				call.response.status(HttpStatusCode.NotFound)
				return@get
			}

			// The following line might throw an error sometimes
			val container = try {
				dockerClient.inspectContainerCmd(server.containerId!!).exec()
			} catch (_: NotFoundException) {
				null
			} catch (_: NullPointerException) {
				null
			}

			val parameters = transaction {
				Parameter.find {
					Parameters.server eq serverId
				}.first()
			}

			val exposedPort = ExposedPort.tcp(25565)
			val port = container?.let { it.networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec }
			val state = container?.let { ServerState.fromContainerState(it.state) }

			val response = DetailedApiServer(
				serverId.toString(),
				server.status,
				container != null,
				container?.name,
				port,
				state,
				ServerParameters.fromParameterEntity(parameters),
			)

			call.respond(response)
		}

		patch {
			val action = try {
				call.receive<ServerActionRequest>().action
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@patch
			}

			val serverId = call.parameters["serverId"] ?: run {
				call.response.status(HttpStatusCode.NotFound)
				return@patch
			}

			val containerId = transaction {
				Server.findById(UUID.fromString(serverId))?.containerId
			} ?: run {
				call.response.status(HttpStatusCode.NotFound)
				return@patch
			}

			when (action) {
				START -> {
					dockerClient.startContainerCmd(containerId).exec()
					call.response.status(HttpStatusCode.NoContent)
				}

				STOP -> {
					try {
						dockerClient.stopContainerCmd(containerId).exec()
					} catch (_: NotModifiedException) {
					}
					call.response.status(HttpStatusCode.NoContent)
				}

				RESTART -> {
					try {
						dockerClient.restartContainerCmd(containerId).exec()
					} catch (_: NotModifiedException) {
					}
					call.response.status(HttpStatusCode.NoContent)
				}

				RECREATE -> {
					recreateServer(UUID.fromString(serverId))
					call.response.status(HttpStatusCode.NoContent)
				}
			}
		}

		put("/parameters") {
			val serverId = try {
				UUID.fromString(call.parameters["serverId"]!!)
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@put
			}

			val parameters = try {
				call.receive<ServerParameters>()
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@put
			}

			transaction {
				val parameter = Parameter.find {
					Parameters.server eq serverId
				}.first()

				parameter.version = parameters.version
				parameter.eula = parameters.eula
				parameter.serverType = parameters.serverType
				parameter.forgeVersion = parameters.forgeVersion
				parameter.fabricLauncherVersion = parameters.fabricLauncherVersion
				parameter.fabricLoaderVersion = parameters.fabricLoaderVersion
				parameter.quiltLauncherVersion = parameters.quiltLauncherVersion
				parameter.quiltLoaderVersion = parameters.quiltLoaderVersion
				parameter.ftbModpackId = parameters.ftbModpackId
				parameter.ftbModpackVersionId = parameters.ftbModpackVersionId
			}

			if (recreateServer(serverId)) {
				call.response.status(HttpStatusCode.OK)
			} else {
				call.response.status(HttpStatusCode.InternalServerError)
			}
		}

		get("/subscription") {
			val server = try {
				val serverId = UUID.fromString(call.parameters["serverId"]!!)
				transaction {
					Server.findById(serverId)!!
				}
			} catch (e: NullPointerException) {
				call.response.status(HttpStatusCode.BadRequest)
				return@get
			}

			try {
				val subscription = Subscription.retrieve(server.subscriptionId)
				val paymentMethod = PaymentMethod.retrieve(subscription.defaultPaymentMethod)
				val invoice = Invoice.retrieve(subscription.latestInvoice)

				val latestInvoice = ApiInvoice(
					Instant.fromEpochSeconds(invoice.periodStart),
					Instant.fromEpochSeconds(invoice.periodEnd),
					invoice.paid,
					invoice.amountDue,
					invoice.amountPaid,
					invoice.amountRemaining,
					invoice.total,
					invoice.currency,
				)

				val info = SubscriptionInfo(
					Instant.fromEpochSeconds(subscription.startDate),
					Instant.fromEpochSeconds(subscription.currentPeriodStart),
					Instant.fromEpochSeconds(subscription.currentPeriodEnd),
					subscription.canceledAt?.let { Instant.fromEpochSeconds(subscription.canceledAt) },
					subscription.status,
					subscription.cancelAtPeriodEnd,
					paymentMethod.type,
					paymentMethod.card?.last4 ?: paymentMethod.sepaDebit?.last4,
					latestInvoice,
				)

				call.respond(info)
			} catch (e: SerializationException) {
				call.response.status(HttpStatusCode.NotFound)
				return@get
			}
		}

		delete("{subscriptionId}") {
			val server = try {
				val serverId = UUID.fromString(call.parameters["serverId"]!!)
				transaction {
					Server.findById(serverId)!!
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
				Subscription.retrieve(server.subscriptionId)
			} catch (_: StripeException) {
				call.response.status(HttpStatusCode.NotFound)
				return@delete
			}

			try {
				if (mode.now) {
					val params = SubscriptionCancelParams.builder()
						.setProrate(false) // TODO: Verify its in the 14 days trial
						.build()

					subscription.cancel(params)
				} else {
					val params = SubscriptionUpdateParams.builder()
						.setCancelAtPeriodEnd(true)
						.build()

					subscription.update(params)
				}

				call.response.status(HttpStatusCode.NoContent)
			} catch (_: StripeException) {
				call.response.status(HttpStatusCode.InternalServerError)
				call.respond(AuthenticationErrors.CANCEL_ERROR.toHashMap(true))
				return@delete
			}
		}
	}
}