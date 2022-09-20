<script>
	import { t } from "svelte-intl-precompile";
	import Icon from "$components/icons/Icon.svelte";
	import Link from "$shared/Link.svelte";
</script>

<div class="rounded-3 bg-light p-lg-5 p-4 my-lg-5 my-3">
	<h2 class="fw-bolder">{$t("hero.not_convinced.title")}</h2>

	<p class="lead d-flex align-items-center gap-2">
		<Icon key="percent" />
		<span>
			{$t("hero.not_convinced.use_code")}
			“<strong>WELCOME</strong>”
			{$t("hero.not_convinced.to_get")}
		</span>
	</p>

	<Link href="/products/" className="btn btn-dark rounded-pill px-4 me-md-2">
		{$t("common.rent_a_server")}
	</Link>
</div>
