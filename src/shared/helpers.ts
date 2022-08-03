import type { ApiError, Id } from "./models";
import type { DetailedServer, Paginate, Server } from "../components/app/models";
import type { ServerParameters } from "../components/app/models";
import type { Token } from "../components/login/models/Token";
import { token } from "../store/store";
import { get } from "svelte/store";
import { goto } from "$app/navigation";

/**
 * Build request's options
 *
 * @param {string?} method
 * @param {string?} raw
 * @returns {Promise<RequestInit>}
 */
function getOptions(method: string = "POST", raw: Object | null = null): RequestInit {
	const jwt = get(token) || "";

	const headers = new Headers();
	headers.append("authorization", `Bearer ${ jwt }`);
	headers.append("content-type", "application/json");
	headers.append("accept", "application/json");

	const body = raw !== null ? JSON.stringify(raw) : null;

	return {
		redirect: "follow",
		mode: "cors",
		headers, body, method,
	};
}

/**
 * Handle API's response
 *
 * @param {Response} response
 * @returns {Promise<{data: Object | null, message: string}>}
 * @throws {message: string, message: number}
 */
function handleResponse(response: Response): Promise<Object | null> {
	return new Promise((resolve, reject) => {
		const contentLength = parseInt(response.headers.get("content-length") ?? "0");

		if (!response.ok) {
			 response.json()
				.then(reject)
				.catch(() => {
					// The json does not exist, just return the HTTP error
					const error: ApiError = {
						code: response.status.toString(),
						isError: true,
						message: response.statusText,
					}
					reject(error);
				});
			 return;
		}

		if (contentLength > 0) {
			response.json()
				.then(resolve)
				.catch(reject);
		} else {
			resolve(null);
		}
	});
}

export function containId(paginate: Paginate<Id>, id: string): boolean {
	for (const element of paginate.data) {
		if (element.id === id) {
			return true;
		}
	}
	return false;
}

export function formatPrice(price: number): string {
	return (price / 100).toFixed(2) + "€";
}

export async function redirect(fallback: string = "/") {
	const search = new URLSearchParams(location.search);
	const redirect = search.get("redirect") ?? fallback;
	await goto(redirect);
}

export async function signUp(email: string, password: string, language: string, acceptTos: boolean): Promise<ApiError> {
	const response = await fetch(
		`${import.meta.env.VITE_API_BASE_URL}authentication/signUp`,
		getOptions("POST", { email, password, language, acceptTos }),
	)
	return await handleResponse(response) as ApiError;
}

export async function signIn(email: string, password: string, code: string | null = null): Promise<Token> {
	const response = await fetch(
		`${import.meta.env.VITE_API_BASE_URL}authentication/signIn`,
		getOptions("POST", { email, password, code }),
	);
	return await handleResponse(response) as Token;
}

export async function getAllServers(page: number = 0): Promise<Paginate<Server>> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server?page=${ page }`, getOptions("GET"))
	return await handleResponse(response) as Paginate<Server>;
}

export async function getServerInfo(selectedServer: string): Promise<DetailedServer> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("GET"))
	return await handleResponse(response) as DetailedServer;
}

export async function patchServer(selectedServer: string, action: string): Promise<null> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("PATCH", { action }))
	return await handleResponse(response) as null;
}

export async function putParameters(selectedServer: string, parameters: ServerParameters): Promise<ServerParameters> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("PUT", parameters));
	return await handleResponse(response) as ServerParameters;
}