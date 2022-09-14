<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import Products from "$components/product/Products.svelte";
</script>

<svelte:head>
	<title>{$t("products")}</title>
</svelte:head>

<h1 class="text-center fw-bolder mb-5">
	{$t("products")}
</h1>

<Products />
