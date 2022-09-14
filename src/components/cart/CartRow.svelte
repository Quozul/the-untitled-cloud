<script lang="ts">
	import type { ApiProduct } from "$models/ApiProduct";
	import { removeFromCart } from "./helpers";
	import { formatPrice } from "$shared/helpers";
	import Icon from "$components/icons/Icon.svelte";

	export let product: ApiProduct;
	export let canEdit = true;
	export let background = false;

	function handleClick() {
		removeFromCart(product);
	}
</script>

<li class="list-group-item d-flex justify-content-between lh-sm px-0" class:bg-light={background}>
	<div>
		<div class="d-flex align-items-center gap-2">
			{#if canEdit}
				<Icon key="x-lg" onClick={handleClick} className="cursor-pointer" />
			{/if}

			<h6 class="my-0">{product.name}</h6>
		</div>

		<small class="text-muted">{product.description}</small>
	</div>

	<span class="fw-bold">{formatPrice(product.price)}</span>
</li>
