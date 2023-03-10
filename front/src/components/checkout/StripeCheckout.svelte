<script lang="ts">
	import { locale, t } from "svelte-intl-precompile";
	import type { PaymentIntentResult, Stripe } from "@stripe/stripe-js/types/stripe-js/stripe";
	import { loadStripe, type PaymentIntent } from "@stripe/stripe-js";
	import { Elements, PaymentElement } from "svelte-stripe";
	import { onDestroy, onMount } from "svelte";
	import { cart, checkoutStep, clientSecret, promoCode } from "$store/store";
	import { CheckoutSteps } from "./constants";
	import { goto } from "$app/navigation";
	import { getClientSecret, updatePaymentIntent } from "./helpers";
	import type { ApiError } from "$shared/models";
	import { AuthenticationErrors } from "$components/login/models/AuthenticationErrors";
	import Link from "$shared/Link.svelte";
	import { formatPrice } from "$shared/helpers";
	import Alert from "$shared/Alert.svelte";
	import { Variant } from "$shared/constants";

	let stripe: Stripe | null = null;
	let processing = false;
	let checkoutError: ApiError = null;
	let elements;
	let cgv = false;
	let totalPrice = 0;
	let paymentIntent: PaymentIntent | null = null;

	function alertUnload(e) {
		e.preventDefault();
		e.returnValue = "";
		return "";
	}

	onMount(async () => {
		window.addEventListener("beforeunload", alertUnload);
		stripe = await loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY);

		if (!$clientSecret) {
			const { error, response } = await getClientSecret();

			if (response) {
				$clientSecret = response.clientSecret;
			}

			checkoutError = error;
		}

		// Get payment intent
		const response = await stripe.retrievePaymentIntent($clientSecret);
		paymentIntent = response.paymentIntent;
		totalPrice = paymentIntent.amount;
	});

	onDestroy(() => {
		window.removeEventListener("beforeunload", alertUnload);
	});

	async function submit() {
		// avoid processing duplicates
		if (processing) return;

		processing = true;

		// confirm payment with stripe
		const result: PaymentIntentResult = await stripe.confirmPayment({
			elements,
			redirect: "if_required",
		});

		if (result.error) {
			// payment failed, notify user
			checkoutError = {
				code: AuthenticationErrors.STRIPE_ERROR,
				isError: true,
				message: result.error.message,
			};
			processing = false;
		} else {
			// Tell the API the payment has been made
			const { error } = await updatePaymentIntent(result.paymentIntent.id);
			if (error) {
				checkoutError = error;
			}

			// Clear everything and redirect to app
			window.removeEventListener("beforeunload", alertUnload);
			await goto(`/${$locale}/dashboard/`);
			$checkoutStep = CheckoutSteps.PRODUCTS;
			$clientSecret = null;
			$cart = null;
			$promoCode = null;
		}
	}
</script>

<Alert>
	<div>
		{$t("checkout.not_activated")}
		<Link href="https://stripe.com/docs/testing#cards">{$t("checkout.test_cards")}</Link>
	</div>
</Alert>

{#if stripe && !!$clientSecret}
	<form on:submit|preventDefault={submit}>
		<Elements
			{stripe}
			clientSecret={$clientSecret}
			bind:elements
			theme="flat"
			variables={{
				colorPrimary: "#000000",
				colorBackground: "#ffffff",
				borderRadius: "0",
			}}
			rules={{
				".Input": {
					border: "1px solid #ced4da",
				},
			}}
		>
			<PaymentElement />
		</Elements>

		<small class="form-check mt-3">
			<input class="form-check-input" type="checkbox" value="" id="cgv" bind:checked={cgv} />
			<label class="form-check-label" for="cgv">
				{$t("i_acknowledge_and_accept")}
				<Link href="/terms-of-sale/">{$t("terms_of_sale")}</Link>
				.
			</label>
		</small>

		<button class="w-100 btn btn-dark my-3" type="submit" disabled={processing || !cgv}>
			{#if processing}
				<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" />
			{/if}
			{$t("checkout.proceed")} ({formatPrice(totalPrice)})
		</button>
	</form>
{:else if !checkoutError}
	<div class="d-flex align-items-center mb-3">
		<div class="spinner-border me-3" role="status" aria-hidden="true" />
		<strong>{$t("loading")}...</strong>
	</div>
{/if}

{#if checkoutError}
	<Alert variant={Variant.DANGER} icon="warning">
		{checkoutError.translatedMessage || checkoutError.message}
	</Alert>
{/if}
