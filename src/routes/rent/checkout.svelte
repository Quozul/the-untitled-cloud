<script>
	import CheckoutElement from "/src/components/checkout/CheckoutElement.svelte";
	import { checkoutStep } from "/src/store/store";
	import { CheckoutSteps } from "/src/components/checkout/constants";

	$checkoutStep = CheckoutSteps.CHECKOUT;
</script>

<svelte:head>
    <title>Paiement</title>
</svelte:head>

<div class="alert alert-info">
    Le service n'étant pas encore activé, je vous invite à utiliser une des
    <a href="https://stripe.com/docs/testing" target="_blank" rel="noreferrer noopener">cartes de tests</a>.
</div>
<CheckoutElement/>