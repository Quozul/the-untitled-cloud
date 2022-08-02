import { CheckoutSteps } from "./constants";
import { cart, checkoutStep, token } from "../../store/store";
import { goto } from "$app/navigation";
import { get } from "svelte/store";

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
	await goto(`/rent/${newStep}/`);
}