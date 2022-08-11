import type { Id } from "$shared/models";
import type { DockerStatus } from "./constants";
import type { ServerParameters } from "$components/parameters/models";
import type { ApiServiceStatus } from "../api/enums/ApiServiceStatus";
import type { ApiSubscriptionStatus } from "../api/enums/ApiSubscriptionStatus";

/**
 * @deprecated Use ApiPaginate instead
 */
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

/**
 * @deprecated Use ApiService instead
 */
export type Server = Id & {
	name: string,
	subscriptionStatus: ApiSubscriptionStatus,
	serverStatus: DockerStatus | null,
}

/**
 * @deprecated Use ServiceState instead
 */
export type ServerState = {
	status: ApiServiceStatus,
	created: boolean,
	running: boolean,
	starting: boolean,
	startedAt: string | null,
	finishedAt: string | null,
}

/**
 * @deprecated Use ApiService instead
 */
export type DetailedServer = Id & {
	subscriptionStatus: ApiSubscriptionStatus,
	name: string | null,
	port: string | null,
	state: ServerState,
	/**
	 * @deprecated
	 */
	parameters: ServerParameters,
}

export enum VersionType {
	RELEASE = "RELEASE",
	SNAPSHOT = "SNAPSHOT",
}

export type Version = Id & {
	type: VersionType,
}

/**
 * @deprecated Use ApiSubscriptionDetails instead
 */
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

/**
 * @deprecated Use ApiInvoice instead
 */
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