package dev.quozul.database.helpers

import com.github.dockerjava.api.model.*
import dev.quozul.containerDirectory
import dev.quozul.database.enums.GameServerE
import dev.quozul.database.models.Container
import dev.quozul.database.models.Server
import dev.quozul.database.models.SubscriptionItem
import dev.quozul.servers.helpers.NameGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class GameServer {
	companion object {
		suspend fun new(subscriptionItem: SubscriptionItem) {
			transaction { subscriptionItem.product.gameServer }?.let {
				when (it) {
					GameServerE.MINECRAFT -> newMinecraftContainer(subscriptionItem, it)
					GameServerE.ARK -> newLinuxGSM(subscriptionItem, it)
					// TODO: Add else branch
				}
			}
		}

		private suspend fun newLinuxGSM(subscriptionItem: SubscriptionItem, gameServer: GameServerE) {
			val product = transaction { subscriptionItem.product }

			// Create port bindings
			val portBindings = Ports()
			for (exposedPort in gameServer.ports) {
				portBindings.bind(exposedPort, Ports.Binding.empty())
			}

			// Create volume bindings
			val baseDirectory = transaction {
				"$containerDirectory${subscriptionItem.subscription.owner.id.value}/${subscriptionItem.id}"
			}

			val volumes = mutableListOf<Volume>()
			val binds = mutableListOf<Bind>()

			gameServer.volumes.forEach {
				val volume = Volume(it.second)
				val bind = Bind("$baseDirectory/${it.first}", volume)

				volumes.add(volume)
				binds.add(bind)
			}

			// Create host config
			val hostConfig: HostConfig = HostConfig
				.newHostConfig()
				.withPortBindings(portBindings)
				.withBinds(binds)
				.withMemory(product.memory.toLong() * 1048576) // Convert MB to B
				.withCpuCount(product.cpu.toLong())

			// Create the container
			val container = transaction {
				subscriptionItem.container ?: Container.new {
					this.name = NameGenerator.getRandomName()
				}
			}

			runBlocking {
				newSuspendedTransaction(Dispatchers.Default) {
					container.dockerContainer = DockerContainer.new(
						image = product.dockerImage,
						tag = container.containerTag,
						env = gameServer.environment,
						hostConfig = hostConfig,
						volumes = volumes,
					)

					subscriptionItem.container = container
				}
			}
		}

		private suspend fun newMinecraftContainer(subscriptionItem: SubscriptionItem, gameServer: GameServerE) {
			val container = transaction {
				subscriptionItem.container ?: Container.new {
					this.name = NameGenerator.getRandomName()
				}
			}

			val server = transaction {
				container.server ?: Server.new {
					this.container = container
				}
			}

			val env = server.toEnvironmentVariables()

			// Create port bindings
			val portBindings = Ports()
			for (exposedPort in gameServer.ports) {
				portBindings.bind(exposedPort, Ports.Binding.empty())
			}

			val volume = Volume("/data")
			val bind = transaction {
				Bind(
					"$containerDirectory${subscriptionItem.subscription.owner.id.value}/${subscriptionItem.id}",
					volume
				)
			}

			runBlocking {
				newSuspendedTransaction(Dispatchers.Default) {
					container.dockerContainer = DockerContainer.new(
						image = subscriptionItem.product.dockerImage,
						tag = container.containerTag,
						env = env,
						hostConfig = HostConfig
							.newHostConfig()
							.withPortBindings(portBindings)
							.withBinds(bind)
							.withMemory(subscriptionItem.product.memory.toLong() * 1048576) // Get resources from product configuration
							.withCpuCount(subscriptionItem.product.cpu.toLong()),
						volumes = listOf(volume),
					)

					subscriptionItem.container = container
				}
			}
		}
	}
}