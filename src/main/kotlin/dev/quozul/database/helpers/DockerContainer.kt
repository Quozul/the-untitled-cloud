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
			tag: String = "latest",
			env: List<String>? = null,
			hostConfig: HostConfig? = null,
			volumes: List<Volume>? = null
		) = createContainer(image, name, tag, env, hostConfig, volumes)?.let {
			DockerContainer(it.id)
		}

		suspend fun createContainer(
			image: String,
			name: String? = null,
			tag: String = "latest",
			env: List<String>? = null,
			hostConfig: HostConfig? = null,
			volumes: List<Volume>? = null
		): CreateContainerResponse? = coroutineScope {
			dockerClient.pullImageCmd(image)
				.withTag(tag)
				.exec(PullImageResultCallback())

			val command = dockerClient.createContainerCmd("$image:$tag")
				.withImage("$image:$tag")

			volumes?.let { command.withVolumes(it) }
			name?.let { command.withName(it) }
			hostConfig?.let { command.withHostConfig(it) }
			env?.let { command.withEnv(it) }

			command.exec()
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

	suspend fun recreate() = coroutineScope {
		launch {
			val previous = inspect()

			// If container is running, stop it
			if (previous.state.running == true) {
				stop()
			}

			remove() // Remove previous container

			createContainer(previous.name)?.let {
				containerId = it.id

				if (previous.state.running == true) {
					start()
				}
			} ?: throw Exception("Container not recreated")
		}
	}
}