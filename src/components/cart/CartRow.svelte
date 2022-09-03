<script lang="ts">
	import type { ApiProduct } from "$models/ApiProduct";
	import { removeFromCart } from "./helpers";
	import { formatPrice } from "$shared/helpers.js";
	import Icon from "$components/icons/Icon.svelte";

	export let product: ApiProduct;
	export let canEdit: boolean = true;

	function handleClick() {
		removeFromCart(product);
	}
</script>

<li class="list-group-item d-flex justify-content-between lh-sm">
	<div>
		<div class="d-flex align-items-center gap-2">
			{#if canEdit}
				<Icon key="x-lg" onClick={handleClick}/>
			{/if}
			<h6 class="my-0">{product.name}</h6>
		</div>
		<small class="text-muted">{product.description}</small>
	</div>
	<span class="text-muted">{formatPrice(product.price)}</span>
</li>