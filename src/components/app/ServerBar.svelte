<script lang="ts">
	import { server } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshAllServers, refreshSelectedServer } from "./helpers";
	import { Variant } from "$shared/constants";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import ServerBarInfo from "$components/app/ServerBarInfo.svelte";
	import { t } from "svelte-intl-precompile";
	import DashboardNav from "$components/navbar/DashboardNav.svelte";
	import Icon from "$components/icons/Icon.svelte";

	// State
	let icon = "warning";

	$: if ($server) {
		if (
			$server.subscription.status === ApiSubscriptionStatus.PENDING ||
			$server.state.pending
		) {
			icon = "hourglass";
		} else if ($server.subscription.status === ApiSubscriptionStatus.SUSPENDED) {
			icon = "pause";
		} else if ($server.subscription.status === ApiSubscriptionStatus.CANCELLED) {
			icon = "archive";
		} else if ($server.state.created) {
			icon = $server.state.running ? "play-fill" : "stop-fill";
		}
	}

	async function fullRefresh() {
		await Promise.all([refreshAllServers(), refreshSelectedServer()]);
	}
</script>

<DashboardNav>
	{#if $server}
		<div class="d-flex gap-2 overflow-hidden align-items-center flex-grow-1">
			<Icon key={icon} width="28" height="28" />
			<span class="fw-bolder m-0 fs-5 server-name">
				{#if $server.subscription.status === ApiSubscriptionStatus.PENDING || $server.state.pending}
					{$t("server_status.pending")}
				{:else if $server.subscription.status === ApiSubscriptionStatus.SUSPENDED}
					{$t("server_status.suspended")}
				{:else if $server.subscription.status === ApiSubscriptionStatus.CANCELLED}
					{$t("server_status.cancelled")}
				{:else if $server.state.created}
					{$server.name ?? $server.product.name}
				{:else}
					{$t("server_status.not_found")}
				{/if}
			</span>

			{#if $server.subscription.active}
				<div class="d-none d-xl-block ms-3">
					<ServerBarInfo />
				</div>
			{/if}
		</div>
	{:else}
		<div class="d-flex gap-2 overflow-hidden align-items-center flex-grow-1">
			<Icon key="hourglass" width="28" height="28" />
			<span class="fw-bolder m-0 fs-5 server-name">
				{$t("common.loading")}
			</span>
		</div>
	{/if}

	<Button icon="arrow-clockwise" variant={Variant.DARK} outline={true} onClick={fullRefresh} />
</DashboardNav>

<style lang="scss">
	.server-name {
		overflow: hidden;
		text-overflow: ellipsis;
	}
</style>
