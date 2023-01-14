<script lang="ts">
	import CheckoutAccordion from "$components/checkout/CheckoutAccordion.svelte";
	import Cart from "$components/cart/Cart.svelte";
	import { checkoutStep, token } from "$store/store";
	import { t } from "svelte-intl-precompile";
	import { CheckoutSteps } from "$components/checkout/constants";
	import Meta from "$components/head/Meta.svelte";

	$checkoutStep = $token ? CheckoutSteps.PROFILE : CheckoutSteps.LOGIN;
</script>

<Meta title={$t("common.checkout")} description={$t("common.checkout_description")}/>

<h1 class="text-center fw-bolder mb-5">
	{$t("common.checkout")}
</h1>

<div class="row g-3">
	<div class="col-12 col-lg-6 order-lg-last">
		<Cart canEdit={$checkoutStep !== CheckoutSteps.CHECKOUT} background={true} />
	</div>

	<div class="col-12 col-lg-6">
		<CheckoutAccordion />
	</div>
</div>
