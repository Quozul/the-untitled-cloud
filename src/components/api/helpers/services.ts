import type { ApiService } from "$models/ApiService";
import type { ApiPaginate } from "$models/ApiPaginate";
import { api, getOptions, handleRequest } from "$shared/helpers";

// TODO: Add filters
export async function getServices(page: number = 0): Promise<ApiPaginate<ApiService>> {
	const options = getOptions("GET");

	const params = new URLSearchParams();
	params.set("page", page.toString());

	const request = api(`service?${params.toString()}`, options);
	return await handleRequest(request) as ApiPaginate<ApiService>;
}

export async function getService(serviceId: string): Promise<ApiService> {
	const options = getOptions("GET");
	const request = api(`service/${serviceId}`, options);
	return await handleRequest(request) as ApiService;
}

export async function patchService(serviceId: string, action: string): Promise<void> {
	const options = getOptions("PATCH", { action });
	const request = api(`service/${serviceId}`, options);
	await handleRequest(request);
}