import type { Id } from "$shared/models";
import type { DockerStatus } from "./constants";
import type { ServerSubscriptionStatus } from "./constants";

export type Paginate<T extends Id> = {
	data: T[],
	firstPage: boolean,
	lastPage: boolean,
	totalPage: number,
	totalElements: number,
	page: number,
}

export const EmptyPaginate: Paginate<any> = {
	data: [],
	firstPage: true,
	lastPage: true,
	totalPage: 0,
	totalElements: 0,
	page: 0,
}

export type Server = Id & {
	name: string,
	subscriptionStatus: ServerSubscriptionStatus,
	serverStatus: DockerStatus | null,
}

export enum ServerStatus {
	RESTARTING = "RESTARTING",
	RUNNING = "RUNNING",
	UNAVAILABLE = "UNAVAILABLE",
	STARTING = "STARTING",
	STOPPED = "STOPPED",
}

export type ServerState = {
	status: ServerStatus,
	created: boolean,
	running: boolean,
	starting: boolean,
	startedAt: string | null,
	finishedAt: string | null,
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
	subscriptionStatus: ServerSubscriptionStatus,
	name: string | null,
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

export type SubscriptionInfo = {
	startDate: string,
	currentPeriodStart: string,
	currentPeriodEnd: string,
	canceledAt: string | null,
	status: string,
	cancelAtPeriodEnd: boolean,
	paymentMethodType: string,
	paymentMethodLast4: string | null,
	latestInvoice: Invoice,
}

export type Invoice = {
	periodStart: string,
	periodEnd: string,
	paid: boolean,
	amountDue: number,
	amountPaid: number,
	amountRemaining: number,
	total: number,
	currency: string,
}