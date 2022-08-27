package dev.quozul.database.helpers

import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.NetworkSettings
import com.github.dockerjava.api.model.Volume
import dev.quozul.dockerClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DockerContainer(var containerId: String) {
	companion object {
		suspend fun new(
			image: String,
			name: String? = null,
			tag: String? = "latest",
			env: List<String>? = null,
			hostConfig: HostConfig? = null,
			volumes: Volume? = null
		) = createContainer(image, name, tag, env, hostConfig, volumes)?.let {
			DockerContainer(it.id)
		}

		suspend fun createContainer(
			image: String,
			name: String? = null,
			tag: String? = "latest",
			env: List<String>? = null,
			hostConfig: HostConfig? = null,
			volumes: Volume? = null
		): CreateContainerResponse? = coroutineScope {
			dockerClient.pullImageCmd(image)
				.withTag(tag)
				.exec(PullImageResultCallback())
				.awaitCompletion()

			dockerClient.createContainerCmd("$image:$tag")
				.withImage("$image:$tag")
				.apply {
					volumes?.let { withVolumes(it) }
					env?.let { withEnv(it) }
					name?.let { withName(it) }
					hostConfig?.let { withHostConfig(it) }
				}
				.exec()
		}
	}

	var name: String
		get() = inspect().name
		set(value) {
			dockerClient.renameContainerCmd(containerId).withName(value).exec()
		}

	val state: ContainerState
		get() = inspect().state

	val networkSettings: NetworkSettings
		get() = inspect().networkSettings

	fun getPort(port: Int = 25565): String? {
		val exposedPort = ExposedPort.tcp(port)
		return networkSettings.ports.bindings[exposedPort]?.first()?.hostPortSpec
	}

	fun inspect() = dockerClient.inspectContainerCmd(containerId).exec()

	fun start() = dockerClient.startContainerCmd(containerId).exec()

	fun stop() = dockerClient.stopContainerCmd(containerId).exec()

	fun restart() = dockerClient.restartContainerCmd(containerId).exec()

	fun remove() = dockerClient.removeContainerCmd(containerId).exec()

	// TODO: Remove all volumes and recreate
	fun reset() {
		TODO("Not implemented")
	}
}