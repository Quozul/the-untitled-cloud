<script lang="ts">
	import CheckoutAccordion from "$components/checkout/CheckoutAccordion.svelte";
	import Cart from "$components/cart/Cart.svelte";
	import { checkoutStep, token } from "$store/store";
	import { t } from "svelte-intl-precompile";
	import { CheckoutSteps } from "$components/checkout/constants";

	$checkoutStep = $token ? CheckoutSteps.PROFILE : CheckoutSteps.LOGIN;
</script>

<svelte:head>
	<title>{$t("checkout")}</title>
</svelte:head>

<h1 class="text-center fw-bolder mb-5">
	{$t("checkout")}
</h1>

<div class="row g-3">
	<div class="col-12 col-lg-6 order-lg-last">
		<Cart canEdit={$checkoutStep !== CheckoutSteps.CHECKOUT} background={true} />
	</div>

	<div class="col-12 col-lg-6">
		<CheckoutAccordion/>
	</div>
</div>