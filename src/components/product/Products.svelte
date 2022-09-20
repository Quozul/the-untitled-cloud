<script lang="ts">
	import { getProducts } from "$components/cart/helpers";
	import type { ApiPaginate } from "$models/ApiPaginate";
	import type { ApiProduct } from "$models/ApiProduct";
	import { onMount } from "svelte";
	import Product from "./Product.svelte";
	import type { ApiError } from "$shared/models";
	import Alert from "$shared/Alert.svelte";
	import { Variant } from "$shared/constants";

	let products: ApiPaginate<ApiProduct>;
	let apiError: ApiError;

	onMount(async () => {
		const { error, response } = await getProducts();
		products = response;
		apiError = error;
	});
</script>

<div class="row g-3">
	{#if products?.data}
		{#each products.data as product}
			<div class="col-12 col-lg-6">
				<Product {product} />
			</div>
		{/each}
	{/if}
</div>

{#if apiError}
	<Alert variant={Variant.DANGER}>
		{apiError.translatedMessage}
	</Alert>
{/if}
