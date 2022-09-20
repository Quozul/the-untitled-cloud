<script lang="ts">
	import Link from "$shared/Link.svelte";
	import { t } from "svelte-intl-precompile";
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>{$t("dashboard_errors.no_server.title")}</h4>
	<p class="lead">{$t("dashboard_errors.no_server.description")}</p>
	<Link href="/products/" className="btn btn-dark">{$t("common.rent_a_server")}</Link>
</div>
