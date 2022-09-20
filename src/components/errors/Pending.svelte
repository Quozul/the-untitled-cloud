<script lang="ts">
	import { refreshAllServers, refreshSelectedServer } from "$components/app/helpers";
	import Button from "$shared/Button.svelte";
	import { t } from "svelte-intl-precompile";

	async function refresh() {
		await Promise.all([refreshAllServers(), refreshSelectedServer()]);
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>{$t("server_status.pending")}</h4>
	<p class="lead">{$t("dashboard_errors.pending.description")}</p>
	<Button onClick={refresh}>{$t("action.refresh")}</Button>
</div>
