import type { ServerParameters } from "./models";
import { getOptions, handleResponse } from "$shared/helpers";

export async function getParameters(selectedServer: string): Promise<ServerParameters> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("GET"));
	return await handleResponse(request) as ServerParameters;
}

export async function putParameters(selectedServer: string, parameters: ServerParameters): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("PUT", parameters));
	await handleResponse(request);
}

export async function putName(selectedServer: string, name: string): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/name`, getOptions("PUT", {name}));
	await handleResponse(request);
}