package dev.quozul.database.extensions.subscriptionItem

import dev.quozul.containerDirectory
import dev.quozul.database.enums.ContainerStatus
import dev.quozul.database.helpers.GameServer
import dev.quozul.database.models.SubscriptionItem
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

suspend fun SubscriptionItem.createContainer(start: Boolean = false) {
	GameServer(this).new()

	if (start) {
		dockerContainer?.start()
	}
}

/**
 * Recreates the container without deleting its data
 */
suspend fun SubscriptionItem.recreate() {
	val wasRunning = isRunning

	// If container is running, stop it
	if (wasRunning) {
		dockerContainer?.stop()
	}

	containerStatus = ContainerStatus.CREATING

	dockerContainer?.remove() // Remove previous container
	dockerContainer = null

	// Create container
	createContainer(wasRunning)

	containerStatus = ContainerStatus.CREATED
}

/**
 * Remove the container and its data
 */
fun SubscriptionItem.remove() {
	containerStatus = ContainerStatus.DELETED

	dockerContainer?.apply {
		stop()
		removeFolder()
		remove()
	}

	transaction {
		container = null
	}
}

private fun SubscriptionItem.removeFolder() {
	val baseDirectory = transaction {
		"$containerDirectory${subscription.owner.id.value}/${this@removeFolder.id}"
	}
	println("Deleting $baseDirectory")

	try {
		val folder = File(baseDirectory)
		folder.deleteRecursively()
	} catch (e: Exception) {
		e.printStackTrace()
	}
}