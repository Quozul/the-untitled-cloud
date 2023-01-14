<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import Products from "$components/product/Products.svelte";
	import Meta from "$components/head/Meta.svelte";
</script>

<Meta title={$t("common.products")} description={$t("common.products_description")}/>

<h1 class="text-center fw-bolder mb-5">
	{$t("common.products")}
</h1>

<Products />
