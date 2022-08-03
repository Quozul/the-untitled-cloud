import type { ApiError, Id } from "./models";
import type { DetailedServer, Paginate, Server } from "../components/app/models";
import type { ServerParameters } from "../components/app/models";
import type { Address } from "../components/address/Address";

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
export function getOptions(method: string = "POST", raw: Object | null = null): RequestInit {
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
export async function handleResponse(response: Response): Promise<Object> {
	return new Promise((resolve, reject) => {
		if (!response.ok) {
			 response.json()
				.then(reject)
				.catch(() => {
					// The json does not exist, just return the HTTP error
					const error: ApiError = {
						// @ts-ignore FIXME
						code: response.status,
						isError: true,
						message: response.statusText,
					}
					reject(error);
				});
		} else {
			if (response.body !== null) {
				response.json()
					.then(resolve)
					.catch(reject);
			} else {
				resolve({
					code: response.status,
					isError: true,
					message: response.statusText,
				});
			}
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

export async function getAddress(): Promise<Address> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}user/address`, getOptions("GET"));
	return await handleResponse(response) as Address;
}

export async function setAddress(address: Address): Promise<void> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}user/address`, getOptions("POST", address));
	await handleResponse(response);
}

export async function getAllServers(page: number = 0): Promise<Paginate<Server>> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server?page=${ page }`, getOptions("GET"))
	return await handleResponse(response) as Paginate<Server>;
}

export async function getServerInfo(selectedServer: string): Promise<DetailedServer> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("GET"))
	return await handleResponse(response) as DetailedServer;
}

export async function patchServer(selectedServer: string, action: string): Promise<void> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}`, getOptions("PATCH", { action }))
	await handleResponse(response);
}

export async function putParameters(selectedServer: string, parameters: ServerParameters): Promise<ServerParameters> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}server/${selectedServer}/parameters`, getOptions("PUT", parameters));
	return await handleResponse(response) as ServerParameters;
}