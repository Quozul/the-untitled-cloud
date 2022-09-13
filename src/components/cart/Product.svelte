<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import { cart } from "$store/store";
	import type { ApiProduct } from "$models/ApiProduct";
	import { formatPrice } from "$shared/helpers.js";
	import { toggleInCart } from "./helpers";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants.js";

	export let product: ApiProduct;

	let isInCart: boolean;

	$: isInCart = !!$cart?.find((p) => p.id === product.id);

	function handleOnClick() {
		toggleInCart(product);
	}
</script>

<div class="border">
	<div class="p-3 d-flex flex-column gap-3">
		<h5 class="my-0 fw-bold">{product.name}</h5>
		<p>{product.description}</p>

		<h1 class="card-title pricing-card-title">
			<span class="fw-bold">{formatPrice(product.price)}</span>
			<small class="text-muted fw-light fs-5">/{$t("month")}</small>
		</h1>

		<Button
			onClick={handleOnClick}
			className="w-100 justify-content-center"
			disabled={!product.inStocks}
			variant={Variant.DARK}
			icon={isInCart ? "bag-dash" : "bag-plus"}
		>
			{#if isInCart}
				{$t("remove_from_cart")}
			{:else if !product.inStocks}
				{$t("out_of_stock")}
			{:else}
				{$t("add_to_cart")}
			{/if}
		</Button>
	</div>

	<hr />

	<div class="p-3">
		<h6 class="fw-bold">{$t("configuration")}</h6>

		<ul>
			<li>{product.cpu} {$t("cpu_core")}</li>
			<li>{product.memory} MB {$t("of")} {$t("memory")}</li>
		</ul>
	</div>
</div>
