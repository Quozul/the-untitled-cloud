<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import type { PaymentIntentResult, Stripe } from "@stripe/stripe-js/types/stripe-js/stripe";
	import { loadStripe } from "@stripe/stripe-js";
	import { Elements, PaymentElement } from "svelte-stripe";
	import { onDestroy, onMount } from "svelte";
	import { cart, checkoutStep, promoCode, selectedServer } from "$store/store";
	import { CheckoutSteps } from "./constants";
	import { goto } from "$app/navigation";
	import { getClientSecret, updatePaymentIntent } from "./helpers";
	import type { ApiError } from "$shared/models";
	import { AuthenticationErrors } from "$components/login/models/AuthenticationErrors";
	import Link from "$shared/Link.svelte";
	import { formatPrice } from "$shared/helpers.js";
	import Alert from "$shared/Alert.svelte";
	import { Variant } from "$shared/constants.js";

	let stripe: Stripe | null = null;
	let processing = false;
	let error: ApiError = null;
	let elements;
	let cgv = false;
	let totalPrice: number = 0;
	let clientSecret: string | null = null;

	function alertUnload(e) {
		e.preventDefault();
		e.returnValue = "";
		return "";
	}

	onMount(async () => {
		window.addEventListener("beforeunload", alertUnload);
		stripe = await loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY);

		try {
			const response = await getClientSecret();
			clientSecret = response.clientSecret;
			totalPrice = response.totalPrice;
		} catch (e: ApiError) {
			error = e;
		}
	});

	onDestroy(() => {
		window.removeEventListener("beforeunload", alertUnload);
	});

	async function submit() {
		// avoid processing duplicates
		if (processing) return;

		processing = true;

		// confirm payment with stripe
		const result: PaymentIntentResult = await stripe
			.confirmPayment({
				elements,
				redirect: "if_required",
			});

		if (result.error) {
			// payment failed, notify user
			error = {
				code: AuthenticationErrors.STRIPE_ERROR,
				isError: true,
				message: result.error.message,
			};
			processing = false;
		} else {
			// Tell the API the payment has been made
			try {
				$selectedServer = await updatePaymentIntent(result.paymentIntent.id)
			} catch (e: ApiError) {
				error = e;
			}

			// Clear everything and redirect to app
			window.removeEventListener("beforeunload", alertUnload);
			await goto("/app");
			$checkoutStep = CheckoutSteps.PRODUCTS;
			clientSecret = null;
			$cart = null;
			$promoCode = null;
		}
	}
</script>

{#if stripe && !!clientSecret}
	<form on:submit|preventDefault={submit}>
		<Elements {stripe} clientSecret={clientSecret} bind:elements>
			<PaymentElement />
		</Elements>

		<small class="form-check mt-3">
			<input class="form-check-input" type="checkbox" value="" id="cgv" bind:checked={cgv}>
			<label class="form-check-label" for="cgv">
				{$t("i_acknowledge_and_accept")}
				<Link href="/cgv/">{$t("terms_of_sale")}</Link>.
			</label>
		</small>

		<button class="w-100 btn btn-primary btn-lg my-3" type="submit" disabled="{processing || !cgv}">
			{#if processing}
				<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
			{/if}
			{$t("checkout.proceed")} ({formatPrice(totalPrice)})
		</button>
	</form>
{:else if !error}
	<div class="d-flex align-items-center mb-3">
		<div class="spinner-border me-3" role="status" aria-hidden="true"></div>
		<strong>{$t("loading")}...</strong>
	</div>
{/if}

{#if error}
	<Alert variant={Variant.DANGER} icon="warning">
		{error.translatedMessage || error.message}
	</Alert>
{/if}