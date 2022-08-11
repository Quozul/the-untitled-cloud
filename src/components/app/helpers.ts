import {
	fetchingServer,
	fetchingServers, fetchServerError,
	fetchServersError, onProfilePage,
	selectedServer, server,
	servers,
} from "$store/store";
import type { DetailedServer, Paginate, Server, SubscriptionInfo } from "./models";
import { containId, getOptions, handleRequest, mergePaginate } from "$shared/helpers";
import { get } from "svelte/store";
import { EmptyPaginate } from "./models";

/**
 * @deprecated Deprecated response typing
 * TODO: Update typings
 */
export async function getAllServers(page: number = 0, ended: boolean = false): Promise<Paginate<Server>> {
	const params = new URLSearchParams();
	params.set("page", page.toString());
	if (ended) {
		params.set("status", "ENDED");
	}

	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}service?${params.toString()}`, getOptions("GET"))
	return await handleRequest(request) as Paginate<Server>;
}

export async function refreshAllServers(page: number = 0): Promise<void> {
	if (page === 0) servers.set(EmptyPaginate);
	fetchServersError.set(null);
	fetchingServers.set(true);

	try {
		const response = await getAllServers(page);
		if (page === 0) servers.set(response);
		else servers.set(mergePaginate(get(servers), response));

		if (!get(onProfilePage)) {
			await setDefaultSelectedServer();
		}
	} catch (error: any) {
		fetchServersError.set(error);
	} finally {
		fetchingServers.set(false);
	}
}

export async function setDefaultSelectedServer(): Promise<void> {
	const s = get(servers);

	const contains = containId(s, get(selectedServer)?.id);

	if (!contains) {
		selectedServer.set(s.data[0]);
		await refreshSelectedServer();
	} else {
		selectedServer.set(contains);
	}
}

/**
 * @deprecated Deprecated response typing
 * TODO: Update typings
 */
export async function getServerInfo(selectedServer: string): Promise<DetailedServer> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}service/${selectedServer}`, getOptions("GET"))
	return await handleRequest(request) as DetailedServer;
}

export async function refreshSelectedServer(): Promise<void> {
	const ss = get(selectedServer)?.id;
	server.set(null);
	if (!ss) return;

	fetchServerError.set(null);
	fetchingServer.set(true);

	try {
		const response = await getServerInfo(ss);
		server.set(response);
	} catch (error: any) {
		fetchServerError.set(error);
	} finally {
		fetchingServer.set(false);
	}

}

/**
 * @deprecated Deprecated route
 * TODO: Update route
 */
export async function patchServer(selectedServer: string, action: string): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("PATCH", { action }))
	await handleRequest(request);
}

/**
 * @deprecated Deprecated route
 * TODO: Update route
 */
export async function getSubscription(selectedServer: string): Promise<SubscriptionInfo> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/subscription`, getOptions("GET"));
	return await handleRequest(request) as SubscriptionInfo;
}

/**
 * @deprecated Deprecated route
 * TODO: Update route
 */
export async function cancelSubscription(selectedServer: string, now: boolean = true): Promise<SubscriptionInfo> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/subscription`, getOptions("DELETE", { now }));
	return await handleRequest(request) as SubscriptionInfo;
}