import { refreshServerInfo, refreshServerList } from "$store/store";
import type { DetailedServer, Paginate, Server, ServerParameters, SubscriptionInfo } from "./models";
import { getOptions, handleResponse } from "$shared/helpers";

export const toggleRefreshServerInfo = () => {
	refreshServerInfo.update(v => !v);
}

export const toggleRefreshServerList = () => {
	refreshServerList.update(v => !v);
}

export async function getAllServers(page: number = 0, ended: boolean = false): Promise<Paginate<Server>> {
	const params = new URLSearchParams();
	params.set("page", page.toString());
	if (ended) {
		params.set("status", "ENDED");
	}

	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server?${params.toString()}`, getOptions("GET"))
	return await handleResponse(request) as Paginate<Server>;
}

export async function getServerInfo(selectedServer: string): Promise<DetailedServer> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("GET"))
	return await handleResponse(request) as DetailedServer;
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