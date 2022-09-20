<script lang="ts">
	import { server } from "$store/store";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { DockerStatus } from "$components/app/constants";
	import { Products } from "$components/cart/constants";
	import { t } from "svelte-intl-precompile";
	import { page } from "$app/stores";
	import type { ApiService } from "$models/ApiService";

	// State
	let duration;
	let formattedStartDate = "Jamais";
	let shortFormattedStartDate = "Jamais";

	async function loadDates(server: ApiService) {
		const { convert, Duration, ZonedDateTime } = await import("@js-joda/core");

		// Parse dates
		const started = ZonedDateTime.parse(server.state.startedAt);
		const convertedDate = convert(started).toDate();

		duration = Duration.between(started, ZonedDateTime.now());
		formattedStartDate = convertedDate.toLocaleDateString("fr-FR", {
			weekday: "long",
			year: "numeric",
			month: "long",
			day: "numeric",
		});

		shortFormattedStartDate = convertedDate.toLocaleDateString("fr-FR", {
			year: "numeric",
			month: "short",
			day: "numeric",
		});
	}

	$: {
		if ($server && $server.state.created) {
			loadDates($server);
		}
	}
</script>

<dl class="m-0 d-flex flex-column flex-xl-row gap-xl-3">
	<div class="separation separation-xl-none justify-content-between flex-xl-column">
		<dt>État</dt>
		<dd class="m-0 text-xl-start">
			{#if $server.state.created && $server.state.running}
				{$t(`server_status.${$server.state.status.toLowerCase()}`)} ({duration?.toMinutes() ||
					0}
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
		<div class="separation separation-xl-none justify-content-between flex-xl-column">
			<dt>Dernier démarrage</dt>
			<dd class="m-0 text-xl-start">
				{#if $server.state?.status === DockerStatus.CREATED}
					Jamais démarré
				{:else}
					<span class="d-inline d-xl-none d-xxl-inline">{formattedStartDate}</span>
					<span class="d-none d-xl-inline d-xxl-none">{shortFormattedStartDate}</span>
				{/if}
			</dd>
		</div>
	{/if}

	<div class="separation separation-xl-none justify-content-between flex-xl-column">
		<dt>Adresse de connexion</dt>
		<dd class="m-0 text-xl-start">
			{#if !$server.port}
				Démarrez le serveur pour avoir une adresse de connexion.
			{:else if $server.product.id === Products.MinecraftServer}
				{$page.url.hostname}:{$server.port}
			{:else if $server.product.id === Products.ArkServer}
				<a href="steam://connect/theuntitledcloud.com:{$server.port}/">
					Lien de connexion Steam
				</a>
			{:else}
				Impossible de déterminer l'adresse de connexion.
			{/if}
		</dd>
	</div>
</dl>
