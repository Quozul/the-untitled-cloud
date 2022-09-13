<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import Products from "$components/cart/Products.svelte";
	import { checkoutStep } from "$store/store";
	import Cart from "$components/cart/Cart.svelte";
	import { CheckoutSteps } from "$components/checkout/constants";
	import Link from "$shared/Link.svelte";
	import { cart } from "$store/store.js";
</script>

<svelte:head>
	<title>{$t("products")}</title>
</svelte:head>

<h1 class="text-center fw-bolder mb-5">
	{$t("products")}
</h1>

<div class="row">
	<div class="col">
		<Products />
	</div>

	<div class="col">
		<Cart canEdit={$checkoutStep !== CheckoutSteps.CHECKOUT} background={true} />

		{#if $cart.length > 0}
			<Link href="/checkout/" className="mt-3 btn btn-dark">{$t("checkout.proceed")}</Link>
		{/if}
	</div>
</div>