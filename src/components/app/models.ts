export type Paginate<T> = {
	data: T[],
	firstPage: boolean,
	lastPage: boolean,
	totalPage: number,
	totalElements: number,
}

export type Server = {
	id: string,
	name: string,
}

export type ServerState = {
	status: string | null,
	running: boolean | null,
	paused: boolean | null,
	restarting: boolean | null,
	oomKilled: boolean | null,
	dead: boolean | null,
	exitCode: number | null,
	error: string | null,
	startedAt: string | null,
	finishedAt: string | null,
	failingStreak: number | null,
}

export type ServerParameters = {
	version: string,
	eula: boolean,
	serverType: string,
	forgeVersion: string | null,
	fabricLauncherVersion: string | null,
	fabricLoaderVersion: string | null,
	quiltLauncherVersion: string | null,
	quiltLoaderVersion: string | null,
	ftbModpackId: number | null,
	ftbModpackVersionId: number | null,
}

export type DetailedServer = {
	id: string,
	name: string,
	port: string | null,
	state: ServerState,
	parameters: ServerParameters,
}

export enum VersionType {
	RELEASE = "RELEASE",
	SNAPSHOT = "SNAPSHOT",
}

export type Version = {
	id: string,
	type: VersionType,
}