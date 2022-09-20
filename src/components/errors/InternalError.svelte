<script lang="ts">
	import Button from "$shared/Button.svelte";
	import { t } from "svelte-intl-precompile";

	export let refresh: VoidFunction;
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>{$t("dashboard_errors.internal_error.title")}</h4>
	<p class="lead">{$t("dashboard_errors.internal_error.description")}</p>
	<Button onClick={refresh}>{$t("common.refresh")}</Button>
</div>
