import type { ApiError, Id } from "./models";
import type { Paginate } from "$components/app/models";
import type { Address } from "$components/address/Address";
import { token } from "$store/store";
import { get } from "svelte/store";
import { goto } from "$app/navigation";
import { defaultLocale } from "./constants";
import { AuthenticationErrors } from "../login/models/AuthenticationErrors";
import { t } from "svelte-intl-precompile";

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
	headers.append("authorization", `Bearer ${jwt}`);
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
 * @param {Promise<Response>} response
 * @returns {Promise<{data: Object | null, message: string}>}
 * @throws {message: string, message: number}
 */
export async function handleResponse(response: Promise<Response>): Promise<Object | null> {
	return new Promise((resolve, reject) => {
		response.then((response) => {
			if (!response.ok) {
				response.json()
					.then((err: ApiError) => {
						reject({
							...err,
							translatedMessage: get(t)(`error.${err.code.toLowerCase()}`),
						})
					})
					.catch(() => {
						// The json does not exist, just return the HTTP error
						const error: ApiError = {
							// @ts-ignore
							code: response.status,
							isError: true,
							message: response.statusText,
						};
						reject(error);
					});
			} else {
				response.json()
					.then(resolve)
					.catch(() => {
						resolve(null);
					});
			}
		})
			.catch(() => {
				const error: ApiError = {
					code: AuthenticationErrors.FETCH_FAILED,
					isError: true,
					message: "Fetch failed",
					translatedMessage: get(t)("error.fetch_failed"),
				};
				reject(error);
			});
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

export const href = (link: string = "", locale: string = ""): string => {
	if (locale === defaultLocale) {
		return link;
	} else {
		return `/${locale}${link}`;
	}
};


export function formatPrice(amount: number, currency: string = "EUR"): string {
	return new Intl.NumberFormat("fr-FR", {style: "currency", currency}).format(amount / 100);
}

export async function redirect(fallback: string = "/") {
	const search = new URLSearchParams(location.search);
	const redirect = search.get("redirect") ?? fallback;
	await goto(redirect);
}

export async function getAddress(): Promise<Address> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}user/address`, getOptions("GET"));
	return await handleResponse(request) as Address;
}

export async function setAddress(address: Address): Promise<void> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}user/address`, getOptions("POST", address));
	await handleResponse(request);
}