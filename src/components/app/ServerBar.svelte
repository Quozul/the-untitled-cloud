<script lang="ts">
	import { server, sidebarCollapsed } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "./helpers";
	import { Variant } from "$shared/constants";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { toggleSidebarCollapsed } from "$components/sidebar/helpers";
	import ServerBarInfo from "$components/app/ServerBarInfo.svelte";

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
</script>

{#if $server}
	<div class="bg-light py-3 d-flex flex-row align-items-center gap-1 gap-lg-3 w-100 px-3">
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
				disabled={!$server.state.created ||
					$server.subscription.status !== ApiSubscriptionStatus.ACTIVE}
				variant={Variant.LIGHT}
				{icon}
				iconSize="28"
			>
				<span class="fw-bolder m-0 fs-5 text-nowrap">
					{#if $server.subscription.status === ApiSubscriptionStatus.PENDING || $server.state.pending}
						En attente
					{:else if $server.subscription.status === ApiSubscriptionStatus.SUSPENDED}
						Suspendu
					{:else if $server.subscription.status === ApiSubscriptionStatus.CANCELLED}
						Terminé
					{:else if $server.state.created}
						{$server.name ?? $server.product.name}
					{:else}
						Introuvable
					{/if}
				</span>
			</Button>
		</div>

		<div class="collapse navbar-collapse d-xl-block" class:show={menu}>
			<ServerBarInfo />
		</div>
	</div>
{:else}
	<div
		class="bg-light py-3 d-flex flex-row align-items-center gap-1 gap-lg-3 gap-lg-5 w-100 px-3"
	>
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
				<span class="fw-bolder m-0 fs-5 text-nowrap"> Introuvable </span>
			</Button>
		</div>
	</div>
{/if}

<style lang="scss">
	.server-bar {
		white-space: nowrap;
	}

	@include media-breakpoint-down(lg) {
		.server-bar {
			width: 100%;
		}
	}
</style>
