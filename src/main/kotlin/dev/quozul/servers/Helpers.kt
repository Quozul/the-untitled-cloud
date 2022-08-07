package dev.quozul.servers

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.*
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import dev.quozul.authentication.User
import dev.quozul.containerDirectory
import dev.quozul.dockerClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

fun getDockerClient(): DockerClient {
	val config: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build()

	val httpClient: DockerHttpClient = ApacheDockerHttpClient.Builder()
		.dockerHost(config.dockerHost)
		.sslConfig(config.sslConfig)
		.maxConnections(100)
		.connectionTimeout(Duration.ofSeconds(30))
		.responseTimeout(Duration.ofSeconds(45))
		.build()

	val client = DockerClientImpl.getInstance(config, httpClient)

	return client
}

fun findContainerFromSubscription(subscriptionId: String): String? =
	findServerFromSubscription(subscriptionId)?.containerId

fun findServerFromSubscription(subscriptionId: String): Server? =
	transaction {
		Server.find {
			Servers.subscriptionId eq subscriptionId
		}.firstOrNull()
	}

fun createOrUpdateServer(owner: User, subscriptionId: String, status: ServerStatus, containerId: String? = null): Server {
	return transaction {
		Server.find {
			Servers.subscriptionId eq subscriptionId
		}.firstOrNull()?.let {
			it.status = status
			it.containerId = containerId

			if (status == ServerStatus.ENDED || status == ServerStatus.SUSPENDED) {
				it.deletionDate = LocalDateTime.now()
			}

			it
		} ?: run {
			val server = Server.new {
				this.owner = owner
				this.subscriptionId = subscriptionId
				this.status = status
				this.containerId = containerId
			}

			Parameter.new {
				this.server = server
				this.eula = true
			}

			server
		}
	}
}

/**
 * This should only be called when a subscription is created.
 */
suspend fun createContainer(
	user: User,
	subscriptionId: String,
) {
	val server = createOrUpdateServer(user, subscriptionId, ServerStatus.ACTIVE)
	createContainer(server)
}

/**
 * This should only be called when a payment has failed.
 */
fun suspendContainer(user: User, subscriptionId: String) {
	findContainerFromSubscription(subscriptionId)?.let {
		try {
			dockerClient.stopContainerCmd(it).exec()
		} catch (_: NotModifiedException) {
		}

		createOrUpdateServer(user, subscriptionId, ServerStatus.SUSPENDED, it)
	}
}

/**
 * This should only be called when a subscription is cancelled.
 */
fun deleteContainer(user: User, subscriptionId: String) {
	findContainerFromSubscription(subscriptionId)?.let {
		try {
			dockerClient.stopContainerCmd(it).exec()
		} catch (_: NotModifiedException) {
		}

		dockerClient.removeContainerCmd(it).exec()

		createOrUpdateServer(user, subscriptionId, ServerStatus.ENDED, null)
		// TODO: Delete volume of container
	}
}

/**
 * Create a new container for the given Server and update it.
 */
suspend fun createContainer(
	server: Server,
	image: String = "itzg/minecraft-server",
	tag: String = "latest",
	start: Boolean = false,
	name: String? = null,
) = coroutineScope {
	val parameters = transaction {
		Parameter.find {
			Parameters.server eq server.id
		}.firstOrNull()?.toEnvironmentVariables()
	} ?: listOf()

	val exposedPort = ExposedPort.tcp(25565)
	val portBindings = Ports()
	portBindings.bind(exposedPort, Ports.Binding.empty())

	val volume = Volume("/data")
	val bind = transaction {
		Bind("${containerDirectory}${server.owner.id.value}/${server.id}", volume)
	}

	launch {
		// TODO: With (custom/previous) name from database, feels weird to have a random name each time
		dockerClient.pullImageCmd(image)
			.withTag(tag)
			.exec(PullImageResultCallback())

		val container = dockerClient.createContainerCmd("$image:$tag")
			.withVolumes(volume)
			.withEnv(parameters)
			.withName(name)
			.withHostConfig(
				HostConfig
					.newHostConfig()
					.withPortBindings(portBindings)
					.withBinds(bind)
					.withMemory(2147483648)
					.withCpuCount(1)
			)
			.exec()

		transaction {
			server.containerId = container.id
		}

		// If the server was already running, rerun it after the recreation
		if (start) {
			try {
				dockerClient.startContainerCmd(container.id).exec()
			} catch (_: NotModifiedException) {
			} catch (_: NotFoundException) {
			}
		}
	}
}

suspend fun recreateServer(serverId: UUID): Boolean {
	val server = try {
		transaction {
			Server.findById(serverId)!!
		}
	} catch (_: NullPointerException) {
		throw Exception("Can't find server")
	}

	// Try stopping then removing the previous container
	val (wasRunning, previousName) = server.containerId?.let {
		val previousContainer = try {
			dockerClient.inspectContainerCmd(it).exec()
		} catch (_: NotFoundException) {
			null
		}

		val wasRunning = previousContainer?.state?.running ?: false
		val previousName = previousContainer?.name

		try {
			dockerClient.stopContainerCmd(it).exec()
		} catch (_: NotModifiedException) {
		} catch (_: NotFoundException) {
		}

		try {
			dockerClient.removeContainerCmd(it).exec()
		} catch (_: NotFoundException) {
		}

		Pair(wasRunning, previousName)
	} ?: Pair(false, null)

	createContainer(server, start = wasRunning, name = previousName)

	return true
}