import type { ClientSecretResponse } from "./models";
import { CheckoutSteps } from "./constants";
import { cart, checkoutStep, token } from "$store/store";
import { goto } from "$app/navigation";
import { get } from "svelte/store";
import { getOptions, handleResponse, href } from "$shared/helpers";
import { locale } from "svelte-intl-precompile";

export async function setStep(newStep: CheckoutSteps): Promise<void> {
	const tok = get(token);

	if (newStep === CheckoutSteps.LOGIN) {
		if (!!tok) {
			await setStep(CheckoutSteps.PROFILE);
			return;
		}
	}

	if (newStep === CheckoutSteps.CHECKOUT || newStep === CheckoutSteps.PROFILE) {
		if (!tok) {
			await setStep(CheckoutSteps.LOGIN);
			return;
		}
	}

	if (newStep === CheckoutSteps.CHECKOUT) {
		if (!get(cart)) {
			await setStep(CheckoutSteps.PRODUCTS);
			return;
		}
	}

	checkoutStep.update(() => newStep);
	await goto(href(`/rent/${newStep}/`, get(locale)));
}

export async function getClientSecret(): Promise<ClientSecretResponse> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}payment/stripe/subscription`, getOptions("POST"))
	return await handleResponse(response) as ClientSecretResponse;
}

export async function updatePaymentIntent(paymentIntentId: string): Promise<void> {
	const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}payment/stripe/subscription`, getOptions("PUT", { paymentIntentId }))
	await handleResponse(response);
}