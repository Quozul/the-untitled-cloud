<script>
	import { t } from "svelte-intl-precompile";
	import Feature from "$components/hero/Feature.svelte";
	import Link from "$shared/Link.svelte";
</script>

<div class="row g-3 justify-content-between my-lg-5 my-3">
	<div class="col-12 col-lg-6 d-flex gap-4 flex-column">
		<Feature title={$t("feature.click_and_play.title")} icon="controller">
			{$t("feature.click_and_play.description")}
		</Feature>

		<Feature title={$t("feature.customer_service.title")} icon="info">
			{$t("feature.customer_service.description")}
		</Feature>
	</div>

	<div class="col-12 col-lg-6 d-flex flex-column align-items-start justify-content-between">
		<div>
			<h2 class="fw-bolder">{$t("hero.server_available_quick.title")}</h2>

			<p>
				{$t("hero.server_available_quick.description")}
			</p>
		</div>

		<Link href="/products/" className="btn btn-dark rounded-pill px-4 me-md-2">
			{$t("common.rent_a_server")}
		</Link>
	</div>
</div>
