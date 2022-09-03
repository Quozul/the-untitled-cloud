import type { ClientSecretResponse } from "./models";
import { api, getOptions, handleRequest } from "$shared/helpers";
import type { ApiService } from "$models/ApiService";
import { get } from "svelte/store";
import { cart, promoCode } from "$store/store";

export async function getClientSecret(): Promise<ClientSecretResponse> {
	const options = getOptions("POST", {
		cart: get(cart).map(p => p.id),
		promo: get(promoCode)?.code ?? null,
	});
	const request = api("payment/stripe/subscription", options);
	return await handleRequest(request) as ClientSecretResponse;
}

export async function updatePaymentIntent(paymentIntentId: string): Promise<ApiService> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}payment/stripe/subscription`, getOptions("PUT", {paymentIntentId}));
	return await handleRequest(request) as ApiService;
}