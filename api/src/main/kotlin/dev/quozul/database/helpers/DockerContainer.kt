package dev.quozul.database.helpers

import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.DockerException
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.NetworkSettings
import com.github.dockerjava.api.model.Volume
import dev.quozul.dockerClient
import kotlinx.coroutines.coroutineScope
import java.io.File

open class DockerContainer(var containerId: String) {
	companion object {
		suspend fun new(
			image: String,
			name: String? = null,
			tag: String? = "latest",
			env: List<String>? = null,
			hostConfig: HostConfig? = null,
			volumes: List<Volume>? = null,
		) = createContainer(image, name, tag, env, hostConfig, volumes)?.let {
			DockerContainer(it.id)
		}

		fun pullImage(
			image: String,
			tag: String? = "latest",
		) {
			dockerClient.pullImageCmd(image)
				.withTag(tag)
				.exec(PullImageResultCallback())
				.awaitCompletion()
		}

		private suspend fun createContainer(
			image: String,
			name: String? = null,
			tag: String? = "latest",
			env: List<String>? = null,
			hostConfig: HostConfig? = null,
			volumes: List<Volume>? = null,
		): CreateContainerResponse? = coroutineScope {
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
		get() = inspect()?.name ?: ""
		set(value) {
			dockerClient.renameContainerCmd(containerId).withName(value).exec()
		}

	val state: ContainerState?
		get() = inspect()?.state

	val networkSettings: NetworkSettings?
		get() = inspect()?.networkSettings

	private fun inspect(): InspectContainerResponse? = try {
		dockerClient.inspectContainerCmd(containerId).exec()
	} catch (_: DockerException) {
		null
	}

	fun start(): Boolean = try {
		dockerClient.startContainerCmd(containerId).exec()
		true
	} catch (_: DockerException) {
		false
	}

	fun stop(): Boolean = try {
		dockerClient.stopContainerCmd(containerId).exec()
		true
	} catch (_: DockerException) {
		false
	}

	fun restart(): Boolean = try {
		dockerClient.restartContainerCmd(containerId).exec()
		true
	} catch (_: DockerException) {
		false
	}

	fun remove(): Boolean = try {
		dockerClient.removeContainerCmd(containerId).exec()
		true
	} catch (_: DockerException) {
		false
	}

	fun removeVolumes() {
		inspect()?.volumes?.forEach {
			println("${it.hostPath}, ${it.containerPath}")
			try {
				val folder = File(it.hostPath)
				folder.deleteRecursively()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}
}