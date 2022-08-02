import type { Id } from "./models";
import type { DetailedServer, Paginate, Server } from "../components/app/models";
import type { Token } from "../components/login/Token";
import { clientSecret, token } from "../store/store";
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
			reject(`${ response.status } ${ response.statusText }`);
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

export async function redirect() {
	const search = new URLSearchParams(location.search);
	const redirect = search.get("redirect") ?? "/";
	await goto(redirect);
}

export async function signUp(email: string, password: string): Promise<Token> {
	const response = await fetch(
		`${import.meta.env.VITE_API_BASE_URL}/authentication/signUp`,
		getOptions("POST", { email, password, }),
	)
	return await handleResponse(response) as Token;
}

export async function signIn(email: string, password: string): Promise<Token> {
	const response = await fetch(
		`${import.meta.env.VITE_API_BASE_URL}/authentication/signIn`,
		getOptions("POST", { email, password, }),
	);
	return await handleResponse(response) as Token;
}

export async function getAllServers(page: number = 0): Promise<Paginate<Server>> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/server?page=${ page }`, getOptions("GET"))
	return await handleResponse(response) as Paginate<Server>;
}

export async function getServerInfo(selectedServer: string): Promise<DetailedServer> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/server/${selectedServer}`, getOptions("GET"))
	return await handleResponse(response) as DetailedServer;
}

export async function patchServer(selectedServer: string, action: string): Promise<null> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/server/${selectedServer}`, getOptions("PATCH", { action }))
	return await handleResponse(response) as null;
}