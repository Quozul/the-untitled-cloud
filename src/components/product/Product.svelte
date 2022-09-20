<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import { cart, cartModalVisible } from "$store/store";
	import type { ApiProduct } from "$models/ApiProduct";
	import { formatPrice } from "$shared/helpers";
	import { toggleInCart } from "$components/cart/helpers";
	import Button from "$shared/Button.svelte";

	export let product: ApiProduct;

	let isInCart: boolean;

	$: isInCart = !!$cart?.find((p) => p.id === product.id);

	function handleOnClick() {
		const added = toggleInCart(product);
		if (added) {
			$cartModalVisible = true;
		}
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
			disabled={!product.inStocks || isInCart}
			outline={isInCart}
			icon={isInCart ? "bag-check" : "bag-plus"}
		>
			{#if isInCart}
				{$t("cart.in_cart")}
			{:else if !product.inStocks}
				{$t("cart.out_of_stock")}
			{:else}
				{$t("cart.add_to_cart")}
			{/if}
		</Button>
	</div>

	<hr />

	<div class="p-3">
		<h6 class="fw-bold">{$t("product.configuration")}</h6>

		<ul>
			<li>{product.cpu} {$t("product.cores")}</li>
			<li>{product.memory} MB {$t("product.of")} {$t("product.memory")}</li>
		</ul>
	</div>
</div>
