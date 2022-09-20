export enum DockerStatus {
	PENDING = "pending",
	CREATED = "created",
	RESTARTING = "restarting",
	RUNNING = "running",
	PAUSED = "paused",
	EXITED = "exited",
	DEAD = "dead",
}

export enum ServerType {
	VANILLA = "VANILLA",
	FORGE = "FORGE",
	FABRIC = "FABRIC",
	QUILT = "QUILT",
	BUKKIT = "BUKKIT",
	SPIGOT = "SPIGOT",
	PAPER = "PAPER",
	PUFFERFISH = "PUFFERFISH",
	PURPUR = "PURPUR",
	MAGMA = "MAGMA",
	MOHIST = "MOHIST",
	CATSERVER = "CATSERVER",
	LOLISERVER = "LOLISERVER",
	CANYON = "CANYON",
	SPONGE_VANILLA = "SPONGE_VANILLA",
	LIMBO = "LIMBO",
	CRUCIBLE = "CRUCIBLE",
	FTBA = "FTBA",
	CURSEFORGE = "CURSEFORGE",
}

export enum ServerTab {
	INFO,
	PARAMETERS,
	FILES,
	CONSOLE,
}
