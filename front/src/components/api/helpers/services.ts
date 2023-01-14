import type { ApiService } from "$models/ApiService";
import type { ApiPaginate } from "$models/ApiPaginate";
import { api, getOptions, handleRequest } from "$shared/helpers";
import type { ApiResponse } from "$shared/models";

// TODO: Add filters
export async function getServices(page = 0): Promise<ApiResponse<ApiPaginate<ApiService>>> {
	const options = getOptions("GET");

	const params = new URLSearchParams();
	params.set("page", page.toString());

	const request = api(`service?${params.toString()}`, options);
	return await handleRequest<ApiPaginate<ApiService>>(request);
}

export async function getService(serviceId: string): Promise<ApiResponse<ApiService>> {
	const options = getOptions("GET");
	const request = api(`service/${serviceId}`, options);
	return await handleRequest<ApiService>(request);
}

export async function patchService(serviceId: string, action: string): Promise<void> {
	const options = getOptions("PATCH", { action });
	const request = api(`service/${serviceId}`, options);
	await handleRequest(request);
}
