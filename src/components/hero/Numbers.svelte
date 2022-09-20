<script lang="ts">
	import Number from "$components/hero/Number.svelte";
	import type { Stats } from "$components/product/models";
	import { onMount } from "svelte";
	import { getStats } from "$components/product/helpers";
	import { t } from "svelte-intl-precompile";

	let stats: Stats = {
		users: 0,
		sold: 0,
		products: 0,
	};

	onMount(async () => {
		const { response } = await getStats();
		if (response) {
			stats = response;
		}
	});
</script>

<div class="container my-lg-5 my-3 d-none d-lg-block">
	<div class="row">
		<Number text={$t("hero.numbers.server_rented")} number="{stats.sold}+" />
		<Number text={$t("hero.numbers.players")} number="{stats.users}+" />
		<Number text={$t("hero.numbers.games_avalable")} number={stats.products} />
	</div>
</div>

<style lang="scss">
	.container {
		margin-right: auto;
		margin-left: auto;
	}
</style>
