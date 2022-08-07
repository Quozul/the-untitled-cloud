<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import { ServerStatus, ServerSubscriptionStatus } from "./constants";
	import { Locale } from "@js-joda/locale_fr";
	import { server } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "./helpers";
	import { ButtonVariant } from "$shared/constants";

	// State
	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let formattedStartDate: string = "Jamais";
	let name: string = "Chargement";

	// Constants
	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	$: {
		if ($server) {
			if ($server.state) {
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
	}

	async function toggleServerState() {
		if ($server.state?.running) {
			await patchServer($server.id, "STOP");
		} else {
			await patchServer($server.id, "START");
		}
		await refreshSelectedServer();
	}
</script>

{#if $server}
<div class="bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5">
	<Button
			onClick={toggleServerState}
			className="d-flex align-items-center"
			disabled="{!$server.serverCreated || $server?.subscriptionStatus !== ServerSubscriptionStatus.ACTIVE}"
			variant={ButtonVariant.LIGHT}
	>
		{#if $server.subscriptionStatus === ServerSubscriptionStatus.PENDING}
			<Icon key="hourglass" width="28" height="28"/>

			<h3 class="m-0">En attente</h3>
		{:else if $server.subscriptionStatus === ServerSubscriptionStatus.SUSPENDED}
			<Icon key="pause" width="28" height="28"/>

			<h3 class="m-0">Suspendu</h3>
		{:else if $server.subscriptionStatus === ServerSubscriptionStatus.ENDED}
			<Icon key="archive" width="28" height="28"/>

			<h3 class="m-0">Terminé</h3>
		{:else if $server.serverCreated && $server.name}
			{#if $server.state?.running}
				<Icon key="play-fill" width="28" height="28"/>
			{:else}
				<Icon key="stop-fill" width="28" height="28"/>
			{/if}

			<h3 class="m-0">{$server.name}</h3>
		{:else}
			<Icon key="warning" width="28" height="28"/>

			<h3 class="m-0">Introuvable</h3>
		{/if}
	</Button>

	<dl class="m-0 d-flex flex-column flex-xl-row gap-3 gap-lg-5">
		<div>
			<dt>État</dt>
			<dd class="m-0">
				{#if $server.serverCreated}
					{$server.state.status}
				{:else if $server.subscriptionStatus === ServerSubscriptionStatus.PENDING}
					En attente
				{:else if $server.subscriptionStatus === ServerSubscriptionStatus.SUSPENDED}
					Suspendu
				{:else if $server.subscriptionStatus === ServerSubscriptionStatus.ENDED}
					Terminé
				{:else}
					Introuvable
				{/if}
			</dd>
		</div>

		{#if $server?.serverCreated}
			<div>
				<dt>Dernier démarrage</dt>
				<dd class="m-0">
					{#if $server.state?.status === ServerStatus.CREATED}
						Jamais démarré
					{:else}
						{formattedStartDate}
					{/if}
				</dd>
			</div>

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
		{/if}
	</dl>
</div>
{:else}
	<div class="placeholder-glow bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5">
		<h3 class="placeholder btn btn-secondary disabled h-100 w-100"></h3>
	</div>
{/if}