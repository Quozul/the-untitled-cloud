<script>
    import { t } from "svelte-intl-precompile";
	import CheckoutElement from "$components/checkout/CheckoutElement.svelte";
	import { checkoutStep } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";

	$checkoutStep = CheckoutSteps.CHECKOUT;
</script>

<svelte:head>
    <title>Paiement</title>
</svelte:head>

<div class="alert alert-info">
    {$t("checkout.not_activated")}
    <a href="https://stripe.com/docs/testing" target="_blank" rel="noreferrer noopener">{$t("checkout.test_cards")}</a>.
</div>
<CheckoutElement/>