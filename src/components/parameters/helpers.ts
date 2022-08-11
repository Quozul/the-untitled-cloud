import type { ServerParameters } from "./models";
import { getOptions, handleRequest } from "$shared/helpers";

/**
 * @deprecated Deprecated route
 * TODO: Update route
 */
export async function getParameters(selectedServer: string): Promise<ServerParameters> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("GET"));
	return await handleRequest(request) as ServerParameters;
}

/**
 * @deprecated Deprecated route
 * TODO: Update route
 */
export async function putParameters(selectedServer: string, parameters: ServerParameters): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("PUT", parameters));
	await handleRequest(request);
}

/**
 * @deprecated Deprecated route
 * TODO: Update route
 */
export async function putName(selectedServer: string, name: string): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/name`, getOptions("PUT", {name}));
	await handleRequest(request);
}