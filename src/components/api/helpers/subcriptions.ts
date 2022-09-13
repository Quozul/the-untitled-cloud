import type { ApiSubscription } from "$models/ApiSubscription";
import type { ApiPaginate } from "$models/ApiPaginate";
import { api, getOptions, handleRequest } from "$shared/helpers";
import type { ApiResponse } from "$shared/models";

// TODO: Add filters
export async function getSubscriptions(page = 0): Promise<ApiResponse<ApiPaginate<ApiSubscription>>> {
	const options = getOptions("GET");

	const params = new URLSearchParams();
	params.set("page", page.toString());

	const request = api(`subscription?${params.toString()}`, options);
	return await handleRequest<ApiPaginate<ApiSubscription>>(request);
}

export async function getSubscription(subscriptionId: string): Promise<ApiResponse<ApiSubscription>> {
	const options = getOptions("GET");
	const request = api(`subscription/${subscriptionId}`, options);
	return await handleRequest<ApiSubscription>(request);
}

export async function deleteSubscription(serviceId: string, now = true): Promise<void> {
	const options = getOptions("DELETE", { now });
	const request = api(`subscription/${serviceId}`, options);
	await handleRequest(request);
}
