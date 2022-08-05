<script>
	import { t } from "svelte-intl-precompile";
	import { token } from "$store/store";
</script>

<h1 class="display-5 fw-bold lh-1 mb-3">
	{$t("hero.title")}
</h1>
<p class="lead">
	{$t("hero.lead")}
</p>

<div class="d-grid gap-2 d-md-flex justify-content-md-start">
	<a href="/rent/products/" class="btn btn-primary btn-lg px-4 me-md-2">{$t("rent_a_server")}</a>

	{#if $token}
		<a href="/app/" class="btn btn-outline-secondary btn-lg px-4">
			{$t("my_servers")}
		</a>
	{:else}
		<a href="/login/?redirect=/app" class="btn btn-outline-secondary btn-lg px-4">
			{$t("to_login")}
		</a>
	{/if}
</div>