<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import StripeCheckout from "./StripeCheckout.svelte";

	let paymentMethod: string | null = null;
</script>

<h4 class="mb-3">{$t("checkout")}</h4>
<div class="my-3">
	<div class="form-check">
		<input
			id="bank"
			value="bank"
			name="paymentMethod"
			type="radio"
			class="form-check-input"
			bind:group={paymentMethod}
		/>
		<label class="form-check-label" for="bank">{$t("payment_method.bank")}</label>
	</div>
	<!--<div class="form-check">
		<input id="paypal" value="paypal" name="paymentMethod" type="radio" class="form-check-input" bind:group={paymentMethod}>
		<label class="form-check-label" for="paypal">{$t("payment_method.paypal")} (soon)</label>
	</div>-->
</div>

<hr class="my-4" />

{#if paymentMethod === "bank"}
	<StripeCheckout />
{:else if paymentMethod === "paypal"}
	N/A
{/if}
