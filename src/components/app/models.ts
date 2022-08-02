import type { Id } from "../../shared/models";

export type Paginate<T extends Id> = {
	data: T[],
	firstPage: boolean,
	lastPage: boolean,
	totalPage: number,
	totalElements: number,
}

export type Server = Id & {
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

export type DetailedServer = Id & {
	name: string,
	port: string | null,
	state: ServerState,
	parameters: ServerParameters,
}

export enum VersionType {
	RELEASE = "RELEASE",
	SNAPSHOT = "SNAPSHOT",
}

export type Version = Id & {
	type: VersionType,
}