import type { ApiSubscription } from "$models/ApiSubscription";
import type { ApiPaginate } from "$models/ApiPaginate";
import { api, getOptions, handleRequest } from "$shared/helpers";

// TODO: Add filters
export async function getSubscriptions(page: number = 0): Promise<ApiPaginate<ApiSubscription>> {
	const options = getOptions("GET");

	const params = new URLSearchParams();
	params.set("page", page.toString());

	const request = api(`subscription?${params.toString()}`, options);
	return (await handleRequest(request)) as ApiPaginate<ApiSubscription>;
}

export async function getSubscription(subscriptionId: string): Promise<ApiSubscription> {
	const options = getOptions("GET");
	const request = api(`subscription/${subscriptionId}`, options);
	return (await handleRequest(request)) as ApiSubscription;
}

export async function deleteSubscription(serviceId: string, now: boolean = true): Promise<void> {
	const options = getOptions("DELETE", { now });
	const request = api(`subscription/${serviceId}`, options);
	await handleRequest(request);
}
