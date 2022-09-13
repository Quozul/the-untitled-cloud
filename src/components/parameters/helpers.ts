import { getOptions, handleRequest } from "$shared/helpers";
import type { ApiServer } from "$models/ApiServer";
import type { ApiService } from "$models/ApiService";

export async function getParameters(selectedServer: string): Promise<ApiServer> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}service/${selectedServer}/server`,
		getOptions("GET")
	);
	return (await handleRequest(request)) as ApiServer;
}

export async function putParameters(selectedServer: string, parameters: ApiServer): Promise<void> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}service/${selectedServer}/server`,
		getOptions("PUT", parameters)
	);
	await handleRequest(request);
}

export async function putService(
	selectedServer: ApiService,
	name: string,
	tag = "latest"
): Promise<void> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}service/${selectedServer.id}`,
		getOptions("PUT", { name, tag })
	);
	await handleRequest(request);
}
