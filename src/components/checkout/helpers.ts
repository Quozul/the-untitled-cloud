import type { ClientSecretResponse } from "./models";
import { api, getOptions, handleRequest } from "$shared/helpers";
import type { ApiService } from "$models/ApiService";
import { get } from "svelte/store";
import { cart, promoCode } from "$store/store";
import type { ApiResponse } from "$shared/models";

export async function getClientSecret(): Promise<ApiResponse<ClientSecretResponse>> {
	const options = getOptions("POST", {
		cart: get(cart).map((p) => p.id),
		promo: get(promoCode)?.code ?? null,
	});
	const request = api("payment/stripe/subscription", options);
	return await handleRequest<ClientSecretResponse>(request);
}

export async function updatePaymentIntent(
	paymentIntentId: string
): Promise<ApiResponse<ApiService>> {
	const request = api("payment/stripe/subscription", getOptions("PUT", { paymentIntentId }));
	return await handleRequest<ApiService>(request);
}

export async function cancelPaymentIntent(
	paymentIntentId: string
): Promise<ApiResponse<ApiService>> {
	const request = api("payment/stripe/subscription", getOptions("DELETE", { paymentIntentId }));
	return await handleRequest<ApiService>(request);
}
