<script lang="ts">
	import type { DetailedServer } from "./models";
	import Icon from "$components/icons/Icon.svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import { ServerStatus } from "./constants";
	import { Locale } from "@js-joda/locale_fr";
	import { ServerSubscriptionStatus } from "./constants";
	import { refreshServerInfo, selectedServer } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { patchServer, toggleRefreshServerInfo } from "./helpers";
	import { ButtonVariant } from "$shared/constants";

	// Props
	export let server: DetailedServer;

	// State
	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let formattedStartDate: string = "Jamais";

	// Constants
	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	$: {
		if (server && server.state) {
			// Parse dates
			started = ZonedDateTime.parse(server.state.startedAt);
			stopped = ZonedDateTime.parse(server.state.finishedAt);
			if (stopped.year() === 1) {
				stopped = ZonedDateTime.now();
			}
			duration = Duration.between(started, stopped);
			formattedStartDate = ZonedDateTime.parse(server.state.startedAt).format(formatter);
		}
	}

	async function toggleServerState() {
		if (server.state?.running) {
			await patchServer($selectedServer.id, "STOP");
		} else {
			await patchServer($selectedServer.id, "START");
		}
		toggleRefreshServerInfo();
	}
</script>

{#if server}
<div class="bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5">
	<Button
			onClick={toggleServerState}
			className="d-flex align-items-center"
			disabled="{server?.subscriptionStatus !== ServerSubscriptionStatus.ACTIVE}"
			variant={ButtonVariant.LIGHT}
	>
		{#if server?.state?.running}
			<Icon key="play-fill" width="28" height="28"/>
		{:else if server?.subscriptionStatus === ServerSubscriptionStatus.PENDING}
			<Icon key="hourglass" width="28" height="28"/>
		{:else}
			<Icon key="stop-fill" width="28" height="28"/>
		{/if}

		<h3 class="m-0">{server?.name || "En attente"}</h3>
	</Button>

	<dl class="m-0 d-flex flex-column flex-xl-row gap-3 gap-lg-5">
		<div>
			<dt>État</dt>
			<dd class="m-0">
				{#if server.serverCreated}
					{server.state.status}
				{:else if server.subscriptionStatus === ServerSubscriptionStatus.PENDING}
					En attente
				{/if}
			</dd>
		</div>

		{#if server?.serverCreated}
			<div>
				<dt>Dernier démarrage</dt>
				<dd class="m-0">
					{#if server.state?.status === ServerStatus.CREATED}
						Jamais démarré
					{:else}
						{formattedStartDate}
					{/if}
				</dd>
			</div>

			<div>
				<dt>Adresse de connexion</dt>
				<dd class="m-0">
					{#if !server.port}
						Démarrez le serveur pour avoir une adresse de connexion.
					{:else}
						quozul.com:{server.port}
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