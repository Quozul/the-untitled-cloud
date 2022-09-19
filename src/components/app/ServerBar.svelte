<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import { DockerStatus } from "./constants";
	import { Locale } from "@js-joda/locale_fr";
	import { server, sidebarCollapsed } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "./helpers";
	import { Variant } from "$shared/constants";
	import { t } from "svelte-intl-precompile";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { Products } from "$components/cart/constants";
	import { page } from "$app/stores";
	import { toggleSidebarCollapsed } from "$components/sidebar/helpers.js";

	// State
	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let formattedStartDate = "Jamais";
	let menu = false;
	let icon = "warning";

	// Constants
	const formatter = DateTimeFormatter.ofPattern("eeee d MMMM yyyy").withLocale(Locale.FRANCE);

	$: {
		if ($server) {
			if ($server.state.created) {
				// Parse dates
				started = ZonedDateTime.parse($server.state.startedAt);
				stopped = ZonedDateTime.parse($server.state.finishedAt);
				if (stopped.year() === 1) {
					stopped = ZonedDateTime.now();
				}
				duration = Duration.between(started, stopped);
				formattedStartDate = ZonedDateTime.parse($server.state.startedAt).format(formatter);
			}

			if ($server.subscription.status === ApiSubscriptionStatus.PENDING || $server.state.pending) {
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
	<div class="bg-light py-3 d-flex flex-row align-items-center gap-1 gap-lg-3 gap-lg-5 w-100 px-3">
		<div class="sidebar-header d-flex align-items-center gap-2">
			<Button icon="list" onClick={toggleSidebarCollapsed} variant={Variant.LIGHT} className="d-inline d-lg-none" />
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

		<div class="collapse navbar-collapse d-lg-block" class:show={menu}>
			<dl class="m-0 d-flex flex-column flex-xl-row gap-3 gap-lg-5">
				<div>
					<dt>État</dt>
					<dd class="m-0">
						{#if $server.state.created && $server.state.running}
							{$t(`server_status.${$server.state.status.toLowerCase()}`)} ({duration.toMinutes()}
							minutes)
						{:else if $server.state.created}
							{$t(`server_status.${$server.state.status.toLowerCase()}`)}
						{:else if $server.subscription.status === ApiSubscriptionStatus.PENDING || $server.state.pending}
							En attente
						{:else if $server.subscription.status === ApiSubscriptionStatus.SUSPENDED}
							Suspendu
						{:else if $server.subscription.status === ApiSubscriptionStatus.CANCELLED}
							Terminé
						{:else}
							Introuvable
						{/if}
					</dd>
				</div>

				{#if $server?.state}
					<div>
						<dt>Dernier démarrage</dt>
						<dd class="m-0">
							{#if $server.state?.status === DockerStatus.CREATED}
								Jamais démarré
							{:else}
								{formattedStartDate}
							{/if}
						</dd>
					</div>
				{/if}

				<div>
					<dt>Adresse de connexion</dt>
					<dd class="m-0">
						{#if !$server.port}
							Démarrez le serveur pour avoir une adresse de connexion.
						{:else if $server.product.id === Products.MinecraftServer}
							{$page.url.hostname}:{$server.port}
						{:else if $server.product.id === Products.ArkServer}
							<a
								href="steam://connect/theuntitledcloud.com:{$server.port}/"
							>Lien de connexion Steam</a
							>
						{:else}
							Impossible de déterminer l'adresse de connexion.
						{/if}
					</dd>
				</div>
			</dl>
		</div>
	</div>
{:else}
	<div
		class="placeholder-glow bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5"
	>
		<h3 class="placeholder btn btn-secondary disabled h-100 w-100">Introuvable</h3>
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
