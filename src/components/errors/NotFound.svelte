<script lang="ts">
	import { patchServer, refreshAllServers, refreshSelectedServer } from "$components/app/helpers";
	import { server } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";
	import type { ApiError } from "$shared/models";
	import Alert from "$shared/Alert.svelte";
	import { t } from "svelte-intl-precompile";

	let apiError: ApiError | null = null;

	async function recreate() {
		const {error, response} = await patchServer($server, "RECREATE");

		if (response) {
			await Promise.all([refreshAllServers(), refreshSelectedServer()]);
		}

		apiError = error;
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>{$t("server_status.not_found")}</h4>
	<p class="lead">{$t("dashboard_errors.not_found.description")}</p>

	<Button variant={Variant.DANGER} onClick={recreate}>{$t("action.recreate")}</Button>

	{#if apiError}
		<Alert variant={Variant.DANGER} icon="warning" className="mt-3">
			{apiError.translatedMessage}
		</Alert>
	{/if}
</div>
