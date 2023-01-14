<script lang="ts">
	import { t } from "svelte-intl-precompile";
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>{$t("common.loading")}</h4>
	<p class="lead">{$t("dashboard_errors.fetching.description")}</p>
</div>
