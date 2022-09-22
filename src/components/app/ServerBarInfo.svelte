<script lang="ts">
	import { server } from "$store/store";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { Products } from "$components/cart/constants";
	import { locale, t } from "svelte-intl-precompile";
	import type { ApiService } from "$models/ApiService";
	import Tooltip from "$components/tooltip/Tooltip.svelte";

	// State
	let duration;
	let formattedStartDate = $t("common.never");
	let shortFormattedStartDate = $t("common.never");
	let serverAddress = "";

	async function loadDates(server: ApiService) {
		const { convert, Duration, ZonedDateTime } = await import("@js-joda/core");

		// Parse dates
		const started = ZonedDateTime.parse(server.state.startedAt);
		if (started.year() === 1) {
			return;
		}

		const convertedDate = convert(started).toDate();

		duration = Duration.between(started, ZonedDateTime.now());
		formattedStartDate = convertedDate.toLocaleDateString($locale as string, {
			weekday: "long",
			year: "numeric",
			month: "long",
			day: "numeric",
		});

		shortFormattedStartDate = convertedDate.toLocaleDateString($locale as string, {
			year: "numeric",
			month: "short",
			day: "numeric",
		});
	}

	$: if ($server) {
		if ($server.state.created) {
			loadDates($server);
		}

		serverAddress = `${import.meta.env.VITE_SERVER_ADDRESS}:${$server.port}`;
	}

	function copyAddress() {
		navigator.clipboard.writeText(serverAddress);
	}
</script>

<dl class="m-0 d-flex flex-column flex-xl-row gap-xl-3 text-nowrap">
	{#if $server}
		<div class="separation separation-xl-none justify-content-between flex-xl-column">
			<dt>{$t("server_bar.state")}</dt>
			<dd class="m-0 text-xl-start">
				{#if $server.state.created && $server.state.running}
					{$t(`server_status.${$server.state.status.toLowerCase()}`)}
					({duration?.toMinutes() || 0} {$t("server_bar.minutes")})
				{:else if $server.state.created}
					{$t(`server_status.${$server.state.status.toLowerCase()}`)}
				{:else if $server.subscription.status === ApiSubscriptionStatus.PENDING || $server.state.pending}
					{$t("server_status.pending")}
				{:else if $server.subscription.status === ApiSubscriptionStatus.SUSPENDED}
					{$t("server_status.suspended")}
				{:else if $server.subscription.status === ApiSubscriptionStatus.CANCELLED}
					{$t("server_status.cancelled")}
				{:else}
					{$t("server_status.not_found")}
				{/if}
			</dd>
		</div>

		{#if $server.state}
			<div class="separation separation-xl-none justify-content-between flex-xl-column">
				<dt>{$t("server_bar.last_startup")}</dt>
				<dd class="m-0 text-xl-start">
					{#if $server.state.created}
						{$t("common.never")}
					{:else}
						<span class="d-inline d-xl-none d-xxl-inline">{formattedStartDate}</span>
						<span class="d-none d-xl-inline d-xxl-none">{shortFormattedStartDate}</span>
					{/if}
				</dd>
			</div>
		{/if}

		<div class="separation separation-xl-none justify-content-between flex-xl-column">
			<dt>{$t("server_bar.address")}</dt>
			<dd class="m-0 text-xl-start">
				{#if !$server.port}
					{$t("server_bar.server_is_stopped")}
				{:else if $server.product.id === Products.MinecraftServer}
					{serverAddress}

					<Tooltip icon="clipboard" onClick={copyAddress}>
						{$t("server_bar.address_copied")}
					</Tooltip>
				{:else if $server.product.id === Products.ArkServer}
					<a href="steam://connect/theuntitledcloud.com:{$server.port}/">
						{$t("server_bar.steam_link")}
					</a>
				{:else}
					{$t("errors.server_address")}
				{/if}
			</dd>
		</div>
	{/if}
</dl>
