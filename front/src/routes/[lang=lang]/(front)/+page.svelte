<script>
	import Hero from "$components/hero/Hero.svelte";
	import Numbers from "$components/hero/Numbers.svelte";
	import NotConvinced from "$components/hero/NotConvinced.svelte";
	import Features from "$components/hero/Features.svelte";
	import Meta from "$components/head/Meta.svelte";
	import { t } from "svelte-intl-precompile";
</script>

<Meta title={$t("common.home")} description={$t("common.home_description")}/>

<div class="px-4 d-flex flex-column gap-5">
	<div class="row flex-lg-row align-items-center g-5 my-lg-5 my-3">
		<div class="col-lg-6">
			<Hero />
		</div>
	</div>

	<Numbers />

	<Features />

	<NotConvinced />
</div>
