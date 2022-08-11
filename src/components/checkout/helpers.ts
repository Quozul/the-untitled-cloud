import type { ClientSecretResponse } from "./models";
import type { Server } from "$components/app/models";
import { getOptions, handleRequest, href } from "$shared/helpers";

export async function getClientSecret(): Promise<ClientSecretResponse> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}payment/stripe/subscription`, getOptions("POST"))
	return await handleRequest(request) as ClientSecretResponse;
}

export async function updatePaymentIntent(paymentIntentId: string): Promise<Server> {
	const request = fetch(`${import.meta.env.VITE_API_BASE_URL}payment/stripe/subscription`, getOptions("PUT", { paymentIntentId }))
	return await handleRequest(request) as Server;
}