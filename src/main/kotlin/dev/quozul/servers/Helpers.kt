package dev.quozul.servers

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
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

	val client = DockerClientImpl.getInstance(config, httpClient)

	return client
}
