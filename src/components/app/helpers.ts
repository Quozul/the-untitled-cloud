import {
	fetchingServer,
	fetchingServers,
	fetchServerError,
	fetchServersError,
	onProfilePage,
	server,
	servers,
} from "$store/store";
import { api, containsService, getOptions, handleRequest, mergePaginate } from "$shared/helpers";
import { get } from "svelte/store";
import { EmptyPaginate } from "./models";
import type { ApiService } from "$models/ApiService";
import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiSubscriptionDetails } from "$models/ApiSubscriptionDetails";
import type { ApiProduct } from "$models/ApiProduct";
import type { ApiUser } from "$models/ApiUser";
import type { ApiBillingPortal } from "$models/ApiBillingPortal";
import type { ApiResponse } from "$shared/models";

export async function getAllServers(
	page = 0,
	ended = false,
): Promise<ApiResponse<ApiPaginate<ApiService>>> {
	const params = new URLSearchParams();
	params.set("page", page.toString());
	if (ended) {
		params.set("status", "CANCELLED");
	}

	const request = api(
		`service?${params.toString()}`,
		getOptions("GET"),
	);
	return await handleRequest<ApiPaginate<ApiService>>(request);
}

export async function refreshAllServers(page = 0): Promise<void> {
	if (page === 0) servers.set(EmptyPaginate);
	fetchServersError.set(null);
	fetchingServers.set(true);

	const { error, response } = await getAllServers(page);

	if (response) {
		if (page === 0) servers.set(response);
		else servers.set(mergePaginate(get(servers), response));

		if (!get(onProfilePage)) {
			await setDefaultSelectedServer();
		}
	} else if (error) {
		fetchServersError.set(error);
	}

	fetchingServers.set(false);
}

/**
 * @deprecated Server does not always have an id anymore
 */
export async function setDefaultSelectedServer(): Promise<void> {
	const s = get(servers);

	const contains = containsService(s, get(server)?.id);

	if (!contains) {
		server.set(s.data[0]);
		await refreshSelectedServer();
	} else {
		server.set(contains);
	}
}

export async function getServerInfo(service: ApiService): Promise<ApiResponse<ApiService>> {
	if (!service.id) {
		return { response: service, error: null };
	}

	const request = api(`service/${service.id}`,getOptions("GET"));

	return await handleRequest<ApiService>(request);
}

export async function refreshSelectedServer(): Promise<void> {
	const ss = get(server);
	server.set(null);
	if (!ss) return;

	fetchServerError.set(null);
	fetchingServer.set(true);

	const { error, response } = await getServerInfo(ss);

	if (response) {
		server.set(response);
	} else if (error) {
		fetchServerError.set(error);
	}

	fetchingServer.set(false);
}

export async function patchServer(service: ApiService, action: string): Promise<void> {
	const request = api(`service/${service.id}`,getOptions("PATCH", { action }));
	await handleRequest(request);
}

export async function getSubscriptionProducts(
	service: ApiService,
): Promise<ApiResponse<ApiPaginate<ApiProduct>>> {
	const request = api(`subscription/${service.subscription.id}/products`,getOptions("GET"));
	return await handleRequest<ApiPaginate<ApiProduct>>(request);
}

export async function getSubscriptionDetails(service: ApiService): Promise<ApiResponse<ApiSubscriptionDetails>> {
	const request = api(`subscription/${service.subscription.id}/details`,getOptions("GET"));
	return await handleRequest<ApiSubscriptionDetails>(request);
}

export async function cancelSubscription(
	service: ApiService,
	now = true,
): Promise<ApiResponse<ApiSubscriptionDetails>> {
	const request = api(`subscription/${service.subscription.id}`,getOptions("DELETE", { now }));
	return await handleRequest<ApiSubscriptionDetails>(request);
}

export async function updateDiscordAccount(code: string, redirectUri: string): Promise<void> {
	const request = api("user/discord",getOptions("POST", { code, redirectUri }));
	await handleRequest(request);
}

export async function getUser(): Promise<ApiResponse<ApiUser>> {
	const request = api("user", getOptions("GET"));
	return await handleRequest<ApiUser>(request);
}

export async function getStripePortal(redirect: string | null = null): Promise<ApiResponse<ApiBillingPortal>> {
	const request = api(`user/portal?redirect=${redirect}`, getOptions("GET"));
	return await handleRequest<ApiBillingPortal>(request);
}
