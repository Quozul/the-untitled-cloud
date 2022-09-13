import { api, getOptions, handleRequest } from "$shared/helpers";
import type { ApiServer } from "$models/ApiServer";
import type { ApiService } from "$models/ApiService";
import type { ApiResponse } from "$shared/models";

export async function getParameters(selectedServer: string): Promise<ApiResponse<ApiServer>> {
	const request = api(
		`service/${selectedServer}/server`,
		getOptions("GET")
	);
	return await handleRequest<ApiServer>(request);
}

export async function putParameters(selectedServer: string, parameters: ApiServer): Promise<void> {
	const request = api(
		`service/${selectedServer}/server`,
		getOptions("PUT", parameters)
	);
	await handleRequest(request);
}

export async function putService(
	selectedServer: ApiService,
	name: string,
	tag = "latest"
): Promise<void> {
	const request = api(
		`service/${selectedServer.id}`,
		getOptions("PUT", { name, tag })
	);
	await handleRequest(request);
}
