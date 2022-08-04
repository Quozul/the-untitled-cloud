<script lang="ts">
	import type { DetailedServer } from "./models";
	import Icon from "../icons/Icon.svelte";
	import { onMount } from "svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import { ServerStatus } from "./constants";
	import { Locale } from "@js-joda/locale_fr";
	import { ServerSubscriptionStatus } from "./constants.js";
	import { patchServer } from "../../shared/helpers";
	import { refreshServer, selectedServer } from "../../store/store";
	import Button from "../shared/Button.svelte";

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

	onMount(async () => {
		if (server.state) {
			// Parse dates
			started = ZonedDateTime.parse(server.state.startedAt);
			stopped = ZonedDateTime.parse(server.state.finishedAt);
			if (stopped.year() === 1) {
				stopped = ZonedDateTime.now();
			}
			duration = Duration.between(started, stopped);
			formattedStartDate = ZonedDateTime.parse(server.state.startedAt).format(formatter);
		}
	});

	async function toggleServerState() {
		console.log(server.state?.running);
		if (server.state?.running) {
			await patchServer($selectedServer.id, "STOP");
		} else {
			await patchServer($selectedServer.id, "START");
		}
		$refreshServer = true;
	}
</script>

<div class="bg-light p-4 d-flex flex-column flex-lg-row align-items-start align-content-lg-center gap-3 gap-lg-5">
	<Button
			onClick={toggleServerState}
			className="btn btn-light bg-white d-flex align-items-center"
			disabled="{server.subscriptionStatus !== ServerSubscriptionStatus.ACTIVE}"
	>
		{#if server.state?.running}
			<Icon key="play-fill" width="28" height="28"/>
		{:else if server.subscriptionStatus === ServerSubscriptionStatus.PENDING}
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

		{#if server.serverCreated}
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