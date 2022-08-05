<script lang="ts">
	import { checkoutStep } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";
	import Product from "$components/cart/Product.svelte";

	$checkoutStep = CheckoutSteps.PRODUCTS;
</script>

<svelte:head>
    <title>Produits</title>
</svelte:head>

<div class="row justify-content-center g-3">
    <div class="col-12 col-lg-6">
        <Product/>
    </div>
</div>