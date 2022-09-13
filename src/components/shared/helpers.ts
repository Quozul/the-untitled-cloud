import type { ApiError, Id } from "./models";
import type { Paginate } from "$components/app/models";
import type { Address } from "$components/address/Address";
import { token } from "$store/store";
import { get } from "svelte/store";
import { goto } from "$app/navigation";
import { defaultLocale } from "./constants";
import { AuthenticationErrors } from "$components/login/models/AuthenticationErrors";
import { t } from "svelte-intl-precompile";
import type { ClassNames } from "./models";
import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiService } from "$models/ApiService";

/**
 * Build request's options
 */
export function getOptions(
	method: string = "POST",
	raw: { [key: string]: any } | null = null
): RequestInit {
	const jwt = get(token) || "";

	const headers = new Headers();
	headers.append("authorization", `Bearer ${jwt}`);
	headers.append("content-type", "application/json");
	headers.append("accept", "application/json");

	const body = raw !== null ? JSON.stringify(raw) : null;

	return {
		redirect: "follow",
		mode: "cors",
		headers,
		body,
		method,
	};
}

/**
 * Handle API response
 */
export async function handleRequest(response: Promise<Response>): Promise<Object | null> {
	return new Promise((resolve, reject) => {
		response
			.then((response) => {
				if (!response.ok) {
					response
						.json()
						.then((err: ApiError) => {
							reject({
								...err,
								translatedMessage: get(t)(`error.${err.code.toLowerCase()}`),
							});
						})
						.catch(() => {
							// The json does not exist, just return the HTTP error
							const error: ApiError = {
								// @ts-ignore
								code: response.status,
								isError: true,
								message: response.statusText,
								translatedMessage: get(t)(`error.${response.status}`),
							};
							reject(error);
						});
				} else {
					response
						.json()
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

export function api(uri: string, options: RequestInit): Promise<Response> {
	return fetch(import.meta.env.VITE_API_BASE_URL + uri, options);
}

export function containId<T extends Id>(paginate: Paginate<T>, id: string | undefined): T | null {
	if (!id) return null;

	for (const element of paginate.data) {
		if (element.id === id) {
			return element;
		}
	}
	return null;
}

/**
 * @deprecated Use <Link/> component instead.
 * @param link
 * @param locale
 */
export const href = (link: string = "", locale: string = ""): string => {
	if (locale === defaultLocale) {
		return link;
	} else {
		return `/${locale}${link}`;
	}
};

export function classNames(classes: ClassNames): string {
	return Object.entries(classes)
		.filter((c) => c[1])
		.map((c) => c[0])
		.join(" ");
}

export function formatPrice(amount: number, currency: string = "EUR"): string {
	return new Intl.NumberFormat("fr-FR", { style: "currency", currency }).format(amount / 100);
}

export async function redirect(fallback: string = "/") {
	const search = new URLSearchParams(location.search);
	const redirect = search.get("redirect") ?? fallback;
	await goto(redirect);
}

export function mergePaginate<T>(a: ApiPaginate<T>, b: ApiPaginate<T>): ApiPaginate<T> {
	return {
		data: [...a.data, ...b.data],
		firstPage: a.firstPage,
		lastPage: b.lastPage,
		totalElements: b.totalElements,
		totalPage: b.totalPage,
		page: b.page,
	};
}

export function capitalize(str: string) {
	return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase().replace("_", " ");
}

// TODO: Move this function
export async function getAddress(): Promise<Address> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}user/address`, getOptions("GET"));
	return (await handleRequest(request)) as Address;
}

// TODO: Move this function
export async function setAddress(address: Address): Promise<void> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}user/address`,
		getOptions("POST", address)
	);
	await handleRequest(request);
}

export function compareServices(service1: ApiService, service2: ApiService): boolean {
	if (!service1 || !service2) {
		return false;
	}

	if (service1.id && service2.id) {
		return service1.id === service2.id;
	}

	return (
		service1.subscription.id === service2.subscription.id &&
		service1.product.id === service2.product.id
	);
}

// This is expensive
export function deepEqual(
	object1: { [key: string]: any },
	object2: { [key: string]: any }
): boolean {
	if (!object1 || !object2) {
		return false;
	}
	const keys1 = Object.keys(object1);
	const keys2 = Object.keys(object2);
	if (keys1.length !== keys2.length) {
		return false;
	}
	for (const key of keys1) {
		const val1 = object1[key];
		const val2 = object2[key];
		const areObjects = isObject(val1) && isObject(val2);
		if ((areObjects && !deepEqual(val1, val2)) || (!areObjects && val1 !== val2)) {
			return false;
		}
	}
	return true;
}

function isObject(object: Object) {
	return object != null && typeof object === "object";
}
