<script lang="ts">
	import StripeCheckout from "./StripeCheckout.svelte";

	let paymentMethod: string | null = null;
</script>

<h4 class="mb-3">Paiement</h4>
<div class="my-3">
	<div class="form-check">
		<input id="bank" value="bank" name="paymentMethod" type="radio" class="form-check-input" bind:group={paymentMethod}>
		<label class="form-check-label" for="bank">Carte bancaire</label>
	</div>
	<div class="form-check">
		<input id="paypal" value="paypal" name="paymentMethod" type="radio" class="form-check-input" bind:group={paymentMethod}>
		<label class="form-check-label" for="paypal">PayPal (bientôt)</label>
	</div>
</div>

<hr class="my-4">

{#if paymentMethod === "bank"}
	<StripeCheckout/>
{:else if paymentMethod === "paypal"}
	N/A
{:else}
	Veuillez choisir une méthode de paiement.
{/if}