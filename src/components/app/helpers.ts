import {
	fetchingServer,
	fetchingServers, fetchServerError,
	fetchServersError,
	selectedServer, server,
	servers,
} from "$store/store";
import type { DetailedServer, Paginate, Server, ServerParameters, SubscriptionInfo } from "./models";
import { containId, getOptions, handleResponse, mergePaginate } from "$shared/helpers";
import { get } from "svelte/store";
import { EmptyPaginate } from "./models";

export async function getAllServers(page: number = 0, ended: boolean = false): Promise<Paginate<Server>> {
	const params = new URLSearchParams();
	params.set("page", page.toString());
	if (ended) {
		params.set("status", "ENDED");
	}

	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server?${params.toString()}`, getOptions("GET"))
	return await handleResponse(request) as Paginate<Server>;
}

export async function refreshAllServers(page: number = 0): Promise<void> {
	if (page === 0) servers.set(EmptyPaginate);
	fetchServersError.set(null);
	fetchingServers.set(true);

	try {
		const response = await getAllServers(page);
		if (page === 0) servers.set(response);
		else servers.set(mergePaginate(get(servers), response));
		await setDefaultSelectedServer();
	} catch (error: any) {
		fetchServersError.set(error);
	} finally {
		fetchingServers.set(false);
	}
}

export async function setDefaultSelectedServer(): Promise<void> {
	const s = get(servers);

	const hasServers: boolean = s.data.length > 0;
	const contains = containId(s, get(selectedServer)?.id);

	if (!contains) {
		selectedServer.set(s.data[0]);
		await refreshSelectedServer();
	} else if (!hasServers) {
		selectedServer.set(null);
	}
}

export async function getServerInfo(selectedServer: string): Promise<DetailedServer> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("GET"))
	return await handleResponse(request) as DetailedServer;
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

export async function patchServer(selectedServer: string, action: string): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("PATCH", { action }))
	await handleResponse(request);
}

export async function putParameters(selectedServer: string, parameters: ServerParameters): Promise<ServerParameters> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("PUT", parameters));
	return await handleResponse(request) as ServerParameters;
}

export async function getSubscription(selectedServer: string): Promise<SubscriptionInfo> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/subscription`, getOptions("GET"));
	return await handleResponse(request) as SubscriptionInfo;
}

export async function cancelSubscription(selectedServer: string, now: boolean = true): Promise<SubscriptionInfo> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/subscription`, getOptions("DELETE", { now }));
	return await handleResponse(request) as SubscriptionInfo;
}