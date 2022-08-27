<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import { DockerStatus } from "./constants";
	import { Locale } from "@js-joda/locale_fr";
	import { server } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "./helpers";
	import { ButtonVariant } from "$shared/constants";
	import { t } from "svelte-intl-precompile";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";

	// State
	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let formattedStartDate: string = "Jamais";
	let name: string = "Chargement";
	let menu: boolean = false;

	// Constants
	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	$: {
		if ($server && $server.state.created) {
			// Parse dates
			started = ZonedDateTime.parse($server.state.startedAt);
			stopped = ZonedDateTime.parse($server.state.finishedAt);
			if (stopped.year() === 1) {
				stopped = ZonedDateTime.now();
			}
			duration = Duration.between(started, stopped);
			formattedStartDate = ZonedDateTime.parse($server.state.startedAt).format(formatter);
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

	function toggleMenu() {
		menu = !menu;
	}
</script>

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

{#if $server}
<div class="bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5">
	<div class="d-flex justify-content-between server-bar">
		<Button
				onClick={toggleServerState}
				className="d-flex align-items-center"
				disabled="{!$server.state.created || $server?.subscription.status !== ApiSubscriptionStatus.ACTIVE}"
				variant={ButtonVariant.LIGHT}
		>
			{#if $server.subscription.status === ApiSubscriptionStatus.PENDING}
				<Icon key="hourglass" width="28" height="28"/>

				<h3 class="m-0">En attente</h3>
			{:else if $server.subscription.status === ApiSubscriptionStatus.SUSPENDED}
				<Icon key="pause" width="28" height="28"/>

				<h3 class="m-0">Suspendu</h3>
			{:else if $server.subscription.status === ApiSubscriptionStatus.CANCELLED}
				<Icon key="archive" width="28" height="28"/>

				<h3 class="m-0">Terminé</h3>
			{:else if $server.state.created}
				{#if $server.state.running}
					<Icon key="play-fill" width="28" height="28"/>
				{:else}
					<Icon key="stop-fill" width="28" height="28"/>
				{/if}

				<h3 class="m-0">{$server.name ?? $server.product.name}</h3>
			{:else}
				<Icon key="warning" width="28" height="28"/>

				<h3 class="m-0">Introuvable</h3>
			{/if}
		</Button>

		<button class="btn btn-light d-lg-none" type="button" on:click|preventDefault={toggleMenu}>
			<Icon key="list" width="28" height="28"/>
		</button>
	</div>

	<div class="collapse navbar-collapse d-lg-block" class:show={menu}>
		<dl class="m-0 d-flex flex-column flex-xl-row gap-3 gap-lg-5">
			<div>
				<dt>État</dt>
				<dd class="m-0">
					{#if $server.state.created && $server.state.running}
						{$t(`server_status.${$server.state.status.toLowerCase()}`)} ({duration.toMinutes()} minutes)
					{:else if $server.state.created}
						{$t(`server_status.${$server.state.status.toLowerCase()}`)}
					{:else if $server.subscription.status === ApiSubscriptionStatus.PENDING}
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
					{:else}
						quozul.com:{$server.port}
					{/if}
				</dd>
			</div>
		</dl>
	</div>
</div>
{:else}
	<div class="placeholder-glow bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5">
		<h3 class="placeholder btn btn-secondary disabled h-100 w-100"></h3>
	</div>
{/if}