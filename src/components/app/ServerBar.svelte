<script lang="ts">
	import { server, sidebarCollapsed } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshAllServers, refreshSelectedServer } from "./helpers";
	import { Variant } from "$shared/constants";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { toggleSidebarCollapsed } from "$components/sidebar/helpers";
	import ServerBarInfo from "$components/app/ServerBarInfo.svelte";
	import { t } from "svelte-intl-precompile";

	// State
	let menu = false;
	let icon = "warning";

	$: {
		if ($server) {
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
	}

	async function toggleServerState() {
		if ($server.state?.running) {
			await patchServer($server, "STOP");
		} else {
			await patchServer($server, "START");
		}
		await refreshSelectedServer();
	}

	async function fullRefresh() {
		await Promise.all([refreshAllServers(), refreshSelectedServer()]);
	}
</script>

<div
	class="bg-light py-3 d-flex align-items-center gap-1 gap-lg-3 w-100 px-3 justify-content-between"
>
	{#if $server}
		<div class="sidebar-header d-flex align-items-center gap-3 overflow-hidden">
			{#if $sidebarCollapsed}
				<Button
					icon="list"
					onClick={toggleSidebarCollapsed}
					variant={Variant.LIGHT}
					className="d-inline d-lg-none"
				/>
			{/if}

			<Button
				onClick={toggleServerState}
				className="d-flex align-items-center overflow-hidden p-0"
				disabled={!$server.state.created ||
					$server.subscription.status !== ApiSubscriptionStatus.ACTIVE}
				variant={Variant.LIGHT}
				{icon}
				iconSize="28"
			>
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
			</Button>

			{#if $server.subscription.active}
				<div class="d-none d-xl-block">
					<ServerBarInfo />
				</div>
			{/if}
		</div>
	{:else}
		<div class="sidebar-header d-flex align-items-center gap-2">
			{#if $sidebarCollapsed}
				<Button
					icon="list"
					onClick={toggleSidebarCollapsed}
					variant={Variant.LIGHT}
					className="d-inline d-lg-none"
				/>
			{/if}

			<Button
				onClick={toggleServerState}
				className="d-flex align-items-center overflow-hidden p-0"
				disabled={true}
				variant={Variant.LIGHT}
				{icon}
				iconSize="28"
			>
				<span class="fw-bolder m-0 fs-5 text-nowrap">{$t("server_status.not_found")}</span>
			</Button>
		</div>
	{/if}

	<Button icon="arrow-clockwise" variant={Variant.DARK} outline={true} onClick={fullRefresh} />
</div>

<style lang="scss">
	.server-bar {
		white-space: nowrap;
	}

	@include media-breakpoint-down(lg) {
		.server-bar {
			width: 100%;
		}
	}

	.sidebar-header {
		.icon {
			flex: 1;
		}

		.server-name {
			overflow: hidden;
			text-overflow: ellipsis;
		}
	}
</style>
