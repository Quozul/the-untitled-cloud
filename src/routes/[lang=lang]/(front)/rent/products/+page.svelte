<script lang="ts">
	import { checkoutStep } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";
	import Products from "$components/cart/Products.svelte";

	$checkoutStep = CheckoutSteps.PRODUCTS;
</script>

<svelte:head>
	<title>Produits</title>
</svelte:head>

<div class="row justify-content-center g-3">
	<Products />
</div>
