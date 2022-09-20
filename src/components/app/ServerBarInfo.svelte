<script lang="ts">
	import { server } from "$store/store";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { DockerStatus } from "$components/app/constants";
	import { Products } from "$components/cart/constants";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import { Locale } from "@js-joda/locale_fr";
	import { t } from "svelte-intl-precompile";
	import { page } from "$app/stores";

	// State
	let duration: Duration = Duration.ZERO;
	let formattedStartDate = "Jamais";

	// Constants
	const formatter = DateTimeFormatter.ofPattern("eeee d MMMM yyyy").withLocale(Locale.FRANCE);

	$: {
		if ($server) {
			if ($server.state.created) {
				// Parse dates
				const started = ZonedDateTime.parse($server.state.startedAt);
				let stopped = ZonedDateTime.parse($server.state.finishedAt);
				if (stopped.year() === 1) {
					stopped = ZonedDateTime.now();
				}
				duration = Duration.between(started, stopped);
				formattedStartDate = ZonedDateTime.parse($server.state.startedAt).format(formatter);
			}
		}
	}
</script>

<dl class="m-0 d-flex flex-column flex-xl-row gap-xl-3">
	<div class="separation separation-xl-none justify-content-between flex-xl-column">
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
		<div class="separation separation-xl-none justify-content-between flex-xl-column">
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

	<div class="separation separation-xl-none justify-content-between flex-xl-column">
		<dt>Adresse de connexion</dt>
		<dd class="m-0">
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