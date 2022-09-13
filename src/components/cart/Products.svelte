<script lang="ts">
	import { getProducts } from "./helpers";
	import type { ApiPaginate } from "$models/ApiPaginate";
	import type { ApiProduct } from "$models/ApiProduct";
	import { onMount } from "svelte";
	import Product from "$components/cart/Product.svelte";

	let products: ApiPaginate<ApiProduct>;

	onMount(async () => {
		const {response} = await getProducts();
		products = response;
	});
</script>

<div class="row g-3">
	{#if products?.data}
		{#each products.data as product}
			<div class="col-6">
				<Product {product} />
			</div>
		{/each}
	{/if}
</div>
