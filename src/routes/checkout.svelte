<script>
	import { loadStripe } from "@stripe/stripe-js";
	import { Elements, PaymentElement } from "svelte-stripe";
	import { onMount } from "svelte";
	import { goto } from "$app/navigation";
	import { token } from "../store.js";

	let stripe = null;
	let processing = false;
	let error = null;
	let clientSecret = null;
	let elements;

	onMount(async () => {
		stripe = await loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY);

		fetch(`${ import.meta.env.VITE_API_BASE_URL }/payment/stripe/subscription`, {
			method: "POST",
			headers: new Headers({ "authorization": `Bearer ${$token}` }),
		})
			.then(res => res.json())
			.then(json => {
				clientSecret = json.clientSecret;
			});
	});

	async function submit() {
		// avoid processing duplicates
		if (processing) return;

		processing = true;

		// confirm payment with stripe
		const result = await stripe
			.confirmPayment({
				elements,
				redirect: "if_required",
			});

		// log results, for debugging
		console.log({ result });

		if (result.error) {
			// payment failed, notify user
			error = result.error;
			processing = false;
		} else {
			// payment succeeded, redirect to "thank you" page
			await goto("/examples/payment-element/thanks");
		}
	}
</script>

{#if stripe && clientSecret}
	<form on:submit|preventDefault={submit}>
		<Elements {stripe} {clientSecret} bind:elements>
			<PaymentElement />
		</Elements>

		<button>Pay</button>
	</form>
{/if}