<script lang="ts">
	import { cart } from "$store/store";
	import { formatPrice } from "./shared/helpers";
	import { t } from "svelte-intl-precompile";
	import CartRow from "$components/cart/CartRow.svelte";

	let total: number = 0;

	$: {
		if ($cart?.length > 0) {
			total = $cart.reduce((a, b) => a + b.price, 0);
		}
	}
</script>

<div class="col-md-5 col-lg-4 order-md-last">
	<h4 class="d-flex justify-content-between align-items-center mb-3">
		<span class="text-primary">{$t("cart")}</span>
	</h4>

	<ul class="list-group mb-3">
		{#if $cart?.length > 0}
			{#each $cart as product}
				<CartRow {product}/>
			{/each}
		{:else}
			<li class="list-group-item d-flex justify-content-between lh-sm">
				<div>
					<h6 class="my-0">{$t("cart_is_empty")}</h6>
				</div>
			</li>
		{/if}
		<li class="list-group-item d-flex justify-content-between">
			<span>{$t("total")} (EUR)</span>
			<strong>{formatPrice(total)}</strong>
		</li>
	</ul>

	<form class="card p-2">
		<div class="input-group">
			<input type="text" class="form-control" placeholder={$t("promo_code")}>
			<button type="submit" class="btn btn-secondary">{$t("use")}</button>
		</div>
	</form>
</div>