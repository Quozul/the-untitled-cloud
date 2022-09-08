package dev.quozul.database.enums

import com.github.dockerjava.api.model.ExposedPort

enum class GameServerE(
	val ports: List<ExposedPort>,
	val volumes: List<Pair<String, String>>,
	val environment: List<String>,
) {
	MINECRAFT(
		listOf(ExposedPort.tcp(25565)),
		listOf(Pair("/", "/data")),
		listOf("EULA=TRUE", "MOTD=Hosted by theuntitledcloud.com"),
	),

	// TODO: Make a custom docker image for this service
	ARK(
		listOf(
			ExposedPort.udp(27015),
			ExposedPort.udp(7777),
			ExposedPort.udp(7778),
		),
		listOf(
			Pair("/serverfiles", "/home/linuxgsm/serverfiles"),
			Pair("/log", "/home/linuxgsm/log"),
			Pair("/config-lgsm", "/home/linuxgsm/lgsm/config-lgsm"),
		),
		listOf("GAMESERVER=arkserver"),
	),
}