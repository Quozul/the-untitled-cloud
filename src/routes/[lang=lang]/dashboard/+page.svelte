<script>
	import NoServer from "$components/errors/NoServer.svelte";
	import DashboardNav from "$components/navbar/DashboardNav.svelte";
	import { t } from "svelte-intl-precompile";
	import { fetchingServers, servers } from "$store/store";
	import Dashboard from "$components/dashboard/Dashboard.svelte";
</script>

<div class="m-lg-3 d-flex flex-column gap-lg-3">
	<DashboardNav>
		<span class="fw-bolder m-0 fs-5">
			{$t("common.dashboard")}
		</span>
	</DashboardNav>

	{#if $servers.totalElements === 0 && !$fetchingServers}
		<NoServer />
	{:else}
		<Dashboard />
	{/if}
</div>