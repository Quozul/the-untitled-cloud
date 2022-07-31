package dev.quozul.servers

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import dev.quozul.authentication.User
import dev.quozul.dockerClient
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

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

fun createContainer(user: User, payment: String, image: String, tag: String = "latest") {
	dockerClient.pullImageCmd(image)
		.withTag(tag)
		.exec(PullImageResultCallback())
		.awaitCompletion()

	val container = dockerClient.createContainerCmd("$image:$tag")
		.withExposedPorts(ExposedPort.tcp(80))
		.exec()

	container.id

	transaction {
		Server.new {
			containerId = container.id
			owner = user
			subscriptionId = payment
		}
	}
}