package dev.quozul.servers.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.github.dockerjava.api.async.ResultCallback
import dev.quozul.database.models.SubscriptionItem
import dev.quozul.dockerClient
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.future.asCompletableFuture
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Closeable
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import com.github.dockerjava.api.model.Frame as DockerFrame


class Console(serverId: UUID, private val socket: WebSocketSession) {
	val outputStream = PipedOutputStream()
	private val scope = CoroutineScope(SupervisorJob())
	private val attach: ResultCallback<DockerFrame>
	private val containerId: String

	init {
		containerId = transaction {
			SubscriptionItem.findById(serverId)!!.container!!.containerId!!
		}

		val callback = object : ResultCallback<DockerFrame> {
			override fun close() {}

			override fun onStart(closeable: Closeable?) {}

			override fun onError(throwable: Throwable?) {}

			override fun onComplete() {}

			override fun onNext(obj: DockerFrame) {
				send(obj.payload)
			}
		}

		val command = dockerClient.execCreateCmd(containerId)
			.withAttachStdin(true)
			.withAttachStdout(true)
			.withTty(true)
			.withCmd("rcon-cli")
			.exec()

		attach = dockerClient.execStartCmd(command.id)
			.withTty(true)
			.withStdIn(PipedInputStream(outputStream))
			.exec(callback)
	}

	fun send(content: ByteArray): CompletableFuture<Unit> = scope.async { socket.send(content) }.asCompletableFuture()

	fun close() {
		attach.close()
	}
}


fun Route.configureConsoleWebsocket() {
	val secret = environment!!.config.property("jwt.secret").getString()
	val issuer = environment!!.config.property("jwt.domain").getString()
	val audience = environment!!.config.property("jwt.audience").getString()

	webSocket("/{serverId}/console") {
		var jwt: DecodedJWT? = null
		var console: Console? = null

		try {
			for (frame in incoming) {
				if (frame is Frame.Text) {
					if (jwt == null) {
						val receivedText = frame.readText()
						receivedText.startsWith("Bearer ")
						val token = receivedText.substring(7)

						// Manually verify JWT
						try {
							val algorithm: Algorithm = Algorithm.HMAC256(secret)
							val verifier = JWT.require(algorithm)
								.withAudience(audience)
								.withIssuer(issuer)
								.build() //Reusable verifier instance
							jwt = verifier.verify(token)

							val serverId = try {
								UUID.fromString(call.parameters["serverId"]!!)
							} catch (e: NullPointerException) {
								close(CloseReason(CloseReason.Codes.NORMAL, "Server not found"))
								return@webSocket
							}

							console = Console(serverId, this)
						} catch (exception: JWTVerificationException) {
							close(CloseReason(CloseReason.Codes.NORMAL, "Not allowed"))
						}
					}
				} else if (frame is Frame.Binary) {
					val receivedBytes = frame.readBytes()
					// Execute command
					withContext(Dispatchers.IO) {
						console?.outputStream?.write(receivedBytes)
					}
				}
			}
		} catch (e: ClosedReceiveChannelException) {
			console?.close()
		} catch (e: Throwable) {
			console?.close()
			e.printStackTrace()
		}

		console?.close()
	}
}