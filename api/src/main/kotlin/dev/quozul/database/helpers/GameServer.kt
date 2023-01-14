package dev.quozul.database.helpers

import com.github.dockerjava.api.model.*
import dev.quozul.containerDirectory
import dev.quozul.database.enums.ContainerStatus
import dev.quozul.database.enums.GameServerE
import dev.quozul.database.extensions.subscriptionItem.containerStatus
import dev.quozul.database.extensions.subscriptionItem.dockerContainer
import dev.quozul.database.extensions.subscriptionItem.gameServer
import dev.quozul.database.models.Container
import dev.quozul.database.models.Server
import dev.quozul.database.models.SubscriptionItem
import dev.quozul.servers.helpers.NameGenerator
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction

class GameServer(private val subscriptionItem: SubscriptionItem) {
	private val portBindings: Ports = Ports()
	private val gameServer: GameServerE = transaction { subscriptionItem.gameServer }
	private val product = transaction { subscriptionItem.product }
	private val volumes = mutableListOf<Volume>()
	private val binds = mutableListOf<Bind>()
	private val hostConfig: HostConfig
	private val service: Container
	private var env: List<String>

	init {
		// Create port bindings
		for (exposedPort in gameServer.ports) {
			portBindings.bind(exposedPort, Ports.Binding.empty())
		}

		// Create volume bindings
		val baseDirectory = transaction {
			"$containerDirectory${subscriptionItem.id}"
		}

		gameServer.volumes.forEach {
			val volume = Volume(it.second)
			val bind = Bind("$baseDirectory/${it.first}", volume)

			volumes.add(volume)
			binds.add(bind)
		}

		// Create host config
		hostConfig = HostConfig
			.newHostConfig()
			.withPortBindings(portBindings)
			.withBinds(binds)
			.withMemory(product.memory.toLong() * 1048576) // Convert MB to B
			.withCpuCount(product.cpu.toLong())

		// Create the container
		service = transaction {
			subscriptionItem.container ?: run {
				val container = Container.new {
					this.name = NameGenerator.getRandomName()
				}
				subscriptionItem.container = container
				container
			}
		}

		// Get environment variables
		env = when (gameServer) {
			GameServerE.MINECRAFT -> {
				transaction {
					service.server ?: Server.new {
						this.container = service
					}
				}.toEnvironmentVariables()
			}

			GameServerE.ARK -> {
				gameServer.environment
			}
		}
	}

	suspend fun new() {
		runBlocking {
			subscriptionItem.containerStatus = ContainerStatus.DOWNLOADING
			DockerContainer.pullImage(product.dockerImage, service.containerTag)

			subscriptionItem.containerStatus = ContainerStatus.CREATING
			subscriptionItem.dockerContainer = DockerContainer.new(
				image = product.dockerImage,
				tag = service.containerTag,
				env = env,
				hostConfig = hostConfig,
				volumes = volumes,
			)

			transaction {
				subscriptionItem.container = service
			}
			subscriptionItem.containerStatus = ContainerStatus.CREATED
		}
	}
}