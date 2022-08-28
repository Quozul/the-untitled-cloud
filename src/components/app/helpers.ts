import {
	fetchingServer,
	fetchingServers, fetchServerError,
	fetchServersError, onProfilePage,
	server,
	servers,
} from "$store/store";
import { containId, getOptions, handleRequest, mergePaginate } from "$shared/helpers";
import { get } from "svelte/store";
import { EmptyPaginate } from "./models";
import type { ApiService } from "$models/ApiService";
import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiSubscriptionDetails } from "$models/ApiSubscriptionDetails";
import type { ApiProduct } from "$models/ApiProduct";
import type { ApiUser } from "$models/ApiUser";

export async function getAllServers(page: number = 0, ended: boolean = false): Promise<ApiPaginate<ApiService>> {
	const params = new URLSearchParams();
	params.set("page", page.toString());
	if (ended) {
		params.set("status", "CANCELLED");
	}

	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}service?${params.toString()}`, getOptions("GET"))
	return await handleRequest(request) as ApiPaginate<ApiService>;
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

/**
 * @deprecated Server does not always have an id anymore
 */
export async function setDefaultSelectedServer(): Promise<void> {
	const s = get(servers);

	const contains = containId(s, get(server)?.id);

	if (!contains) {
		server.set(s.data[0]);
		await refreshSelectedServer();
	} else {
		server.set(contains);
	}
}

export async function getServerInfo(service: ApiService): Promise<ApiService> {
	if (!service.id) {
		return service;
	}

	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}service/${service.id}`, getOptions("GET"))
	return await handleRequest(request) as ApiService;
}

export async function refreshSelectedServer(): Promise<void> {
	const ss = get(server);
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

export async function patchServer(service: ApiService, action: string): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}service/${service.id}`, getOptions("PATCH", {action}));
	await handleRequest(request);
}

export async function getSubscriptionProducts(service: ApiService): Promise<ApiPaginate<ApiProduct>> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}subscription/${service.subscription.id}/products`, getOptions("GET"));
	return await handleRequest(request) as ApiPaginate<ApiProduct>;
}

export async function getSubscriptionDetails(service: ApiService): Promise<ApiSubscriptionDetails> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}subscription/${service.subscription.id}/details`, getOptions("GET"));
	return await handleRequest(request) as ApiSubscriptionDetails;
}

export async function cancelSubscription(service: ApiService, now: boolean = true): Promise<ApiSubscriptionDetails> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}subscription/${service.subscription.id}`, getOptions("DELETE", { now }));
	return await handleRequest(request) as ApiSubscriptionDetails;
}

export async function updateDiscordAccount(code: string, redirectUri: string) {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}user/discord`, getOptions("POST", { code, redirectUri }));
	await handleRequest(request);
}

export async function getUser(): Promise<ApiUser> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}user`, getOptions("GET"));
	return await handleRequest(request) as ApiUser;
}