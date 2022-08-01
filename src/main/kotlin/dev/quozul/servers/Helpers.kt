package dev.quozul.servers

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import dev.quozul.authentication.User
import dev.quozul.dockerClient
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

	return DockerClientImpl.getInstance(config, httpClient)
}

fun findContainerFromSubscription(subscriptionId: String): String? {
	return transaction {
		Server.find {
			Servers.subscriptionId eq subscriptionId
		}.first().containerId
	}
}

fun createOrUpdateServer(owner: User, subscriptionId: String, status: ServerStatus, containerId: String?) {
	transaction {
		Server.find {
			Servers.subscriptionId eq subscriptionId
		}.firstOrNull()?.let {
			it.status = status
			it.containerId = containerId

			if (status == ServerStatus.ENDED || status == ServerStatus.SUSPENDED) {
				it.deletionDate = LocalDateTime.now()
			}
		} ?: run {
			val server = Server.new {
				this.owner = owner
				this.subscriptionId = subscriptionId
				this.status = status
				this.containerId = containerId
			}

			Parameter.new {
				this.server = server
			}
		}
	}
}

fun createContainer(
	user: User,
	subscriptionId: String,
	image: String = "itzg/minecraft-server",
	tag: String = "latest"
) {
	dockerClient.pullImageCmd(image)
		.withTag(tag)
		.exec(PullImageResultCallback())
		.awaitCompletion()

	val container = dockerClient.createContainerCmd("$image:$tag")
		.withExposedPorts(ExposedPort.tcp(80))
		.exec()

	createOrUpdateServer(user, subscriptionId, ServerStatus.ACTIVE, container.id)
}

fun suspendContainer(user: User, subscriptionId: String) {
	findContainerFromSubscription(subscriptionId)?.let {
		try {
			dockerClient.stopContainerCmd(it).exec()
		} catch (_: NotModifiedException) {
		}

		createOrUpdateServer(user, subscriptionId, ServerStatus.SUSPENDED, it)
	}
}

fun deleteContainer(user: User, subscriptionId: String) {
	findContainerFromSubscription(subscriptionId)?.let {
		try {
			dockerClient.stopContainerCmd(it).exec()
		} catch (_: NotModifiedException) {
		}

		dockerClient.removeContainerCmd(it).exec()

		createOrUpdateServer(user, subscriptionId, ServerStatus.ENDED, null)
	}
}

fun recreateServer(serverId: UUID): Boolean {
	val server = try {
		transaction {
			Server.findById(serverId)!!
		}
	} catch (_: NullPointerException) {
		throw Exception("Can't find server")
	}

	// Try stopping then removing the previous container
	server.containerId?.let {
		try {
			dockerClient.stopContainerCmd(it).exec()
		} catch (_: NotModifiedException) {
		} catch (_: NotFoundException) {
		}

		try {
			dockerClient.removeContainerCmd(it).exec()
		} catch (_: NotFoundException) {
		}
	}

	// TODO: Get previous used image
	// TODO: Make this asynchronous
	dockerClient.pullImageCmd("itzg/minecraft-server")
		.withTag("latest")
		.exec(PullImageResultCallback())
		.awaitCompletion()

	val parameters = transaction {
		Parameter.find {
			Parameters.server eq serverId
		}.firstOrNull()?.toEnvironmentVariables()
	} ?: listOf()

	val exposedPort = ExposedPort.tcp(25565)
	val portBindings = Ports()
	portBindings.bind(exposedPort, Ports.Binding.empty())

	val container = dockerClient.createContainerCmd("itzg/minecraft-server:latest")
		.withHostConfig(
			HostConfig
				.newHostConfig()
				.withPortBindings(portBindings)
		)
		.withEnv(parameters)
		.exec()

	transaction {
		server.containerId = container.id
	}

	return true
}