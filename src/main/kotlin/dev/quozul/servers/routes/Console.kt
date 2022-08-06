package dev.quozul.servers.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.github.dockerjava.api.async.ResultCallback
import dev.quozul.dockerClient
import dev.quozul.servers.Server
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.future.asCompletableFuture
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.*
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
			Server.findById(serverId)!!.containerId!!
		}

		val callback = object : ResultCallback<DockerFrame> {
			override fun close() {
				println("close")
			}

			override fun onStart(closeable: Closeable?) {
				println("start")
			}

			override fun onError(throwable: Throwable?) {
				println("error")
			}

			override fun onComplete() {
				println("completed")
			}

			override fun onNext(obj: DockerFrame) {
				send(obj.payload.decodeToString())
			}
		}

		attach = dockerClient.attachContainerCmd(containerId)
			.withStdIn(PipedInputStream(outputStream))
			.withStdOut(true)
			.withLogs(true)
			.withFollowStream(true)
			.exec(callback)
	}

	fun send(str: String): CompletableFuture<Unit> = scope.async { socket.send(str) }.asCompletableFuture()

	fun close() {
		attach.close()
	}

	fun exec(command: String) {
		val cmd = dockerClient.execCreateCmd(containerId)
			.withCmd("mc-send-to-console", command)
			.exec()

		dockerClient.execStartCmd(cmd.id).exec(null)
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
					val receivedText = frame.readText()
					if (jwt == null) {
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
					} else {
						// Execute command
						console?.exec(receivedText)
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