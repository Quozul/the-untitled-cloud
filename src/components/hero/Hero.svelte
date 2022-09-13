<script>
	import { locale, t } from "svelte-intl-precompile";
	import { token } from "$store/store";
	import Link from "$shared/Link.svelte";
</script>

<h1 class="display-5 fw-bolder lh-1 mb-3">
	The Untitled Cloud
</h1>

<p class="lead my-5">
	{$t("hero.lead")}
</p>

<div class="d-grid gap-2 d-md-flex justify-content-md-start">
	<Link href="/rent/products/" className="btn btn-dark rounded-pill btn-lg px-4 me-md-2">
		{$t("rent_a_server")}
	</Link>

	{#if $token}
		<Link href="/app/" className="btn btn-outline-dark rounded-pill btn-lg px-4">
			{$t("my_servers")}
		</Link>
	{:else}
		<Link
			href="/login/?redirect=/{$locale}/app"
			className="btn btn-outline-dark rounded-pill btn-lg px-4"
		>
			{$t("to_login")}
		</Link>
	{/if}
</div>
