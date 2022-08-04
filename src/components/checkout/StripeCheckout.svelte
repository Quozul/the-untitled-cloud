<script lang="ts">
	import type { PaymentIntentResult, Stripe } from "@stripe/stripe-js/types/stripe-js/stripe";
	import { loadStripe } from "@stripe/stripe-js";
	import { Elements, PaymentElement } from "svelte-stripe";
	import { onDestroy, onMount } from "svelte";
	import { cart, checkoutStep, token, clientSecret } from "../../store/store.ts";
	import { CheckoutSteps } from "./constants";
	import { goto } from "$app/navigation";
	import { getClientSecret, updatePaymentIntent } from "./helpers";

	let stripe: Stripe | null = null;
	let processing = false;
	let error = null;
	let elements;
	let cgv = false;
	let eula = false;

	function alertUnload(e) {
		e.preventDefault();
		e.returnValue = "";
		return "";
	}

	onMount(async () => {
		window.addEventListener("beforeunload", alertUnload);
		stripe = await loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY);

		if (!$clientSecret) {
			const response = await getClientSecret();
			$clientSecret = response.clientSecret;
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
			error = result.error;
			processing = false;
		} else {
			// Tell the API the payment has been made
			await updatePaymentIntent(result.paymentIntent.id);

			// Clear everything and redirect to app
			window.removeEventListener("beforeunload", alertUnload);
			await goto("/app");
			$checkoutStep = CheckoutSteps.PRODUCTS;
			$clientSecret = null;
			$cart = null;
		}
	}
</script>

{#if stripe && !!$clientSecret}
	<form on:submit|preventDefault={submit}>
		<Elements {stripe} clientSecret={$clientSecret} bind:elements>
			<PaymentElement />
		</Elements>

		<small class="form-check mt-3">
			<input class="form-check-input" type="checkbox" value="" id="cgv" bind:checked={cgv}>
			<label class="form-check-label" for="cgv">
				J'ai pris connaissance et j'accepte les
				<a href="/cgv">conditions générales de vente</a>
				du site.
			</label>
		</small>

		<small class="form-check">
			<input class="form-check-input" type="checkbox" value="" id="eula"bind:checked={eula}>
			<label class="form-check-label" for="eula">
				J'ai pris connaissance et j'accepte le
				<a href="https://www.minecraft.net/fr-fr/eula" target="_blank" rel="noreferrer noopener">contrat de licence utilisateur final</a>
				de Minecraft.
			</label>
		</small>

		<button class="w-100 btn btn-primary btn-lg mt-3" type="submit" disabled="{processing || !cgv || !eula}">
			{#if processing}
				<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
			{/if}
			Procéder au paiement
		</button>
	</form>
{:else}
	<div class="d-flex align-items-center">
		<div class="spinner-border me-3" role="status" aria-hidden="true"></div>
		<strong>Chargement...</strong>
	</div>
{/if}