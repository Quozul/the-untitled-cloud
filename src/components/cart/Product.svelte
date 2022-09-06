<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import { cart } from "$store/store";
	import type { ApiProduct } from "$models/ApiProduct";
	import { formatPrice } from "$shared/helpers.js";
	import { toggleInCart } from "./helpers";
	import Button from "$shared/Button.svelte";

	export let product: ApiProduct;

	let isInCart: boolean;

	$: isInCart = !!$cart?.find(p => p.id === product.id);

	function handleOnClick() {
		toggleInCart(product);
	}
</script>

<div class="card">
	<div class="card-header py-4">
		<h4 class="my-0 fw-normal">{product.name}</h4>
	</div>

	<div class="card-body">
		<h1 class="card-title pricing-card-title">
			{formatPrice(product.price)}<small class="text-muted fw-light">/{$t("month")}</small>
		</h1>

		<ul class="list-unstyled mt-3">
			<li>{product.cpu} {$t("cpu_core")}</li>
<!--			<li>{nf(product.memory * 1048576, "0 B")} {$t("of")} {$t("memory")}</li>-->
		</ul>

		<Button
			onClick={handleOnClick}
			className="w-100 btn-lg text-center"
			outline={!isInCart}
			disabled={!product.inStocks}
		>
			{#if isInCart}
				{$t("remove_from_cart")}
			{:else if !product.inStocks}
				{$t("out_of_stock")}
			{:else}
				{$t("choose")}
			{/if}
		</Button>
	</div>
</div>