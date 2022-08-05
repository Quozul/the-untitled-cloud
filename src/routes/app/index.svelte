<script lang="ts">
	import ServerInfo from "../../components/app/tabs/info/ServerInfo.svelte";
	import Sidebar from "../../components/app/sidebar/Sidebar.svelte";
	import { token, selectedServer, selectedTab, refreshServerInfo } from "../../store/store";
	import { goto } from "$app/navigation";
	import { onDestroy, onMount } from "svelte";
	import NoServer from "../../components/app/tabs/info/NoServer.svelte";
	import type { DetailedServer, Server } from "../../components/app/models";
	import ServerBar from "../../components/app/ServerBar.svelte";
	import ServerFtp from "../../components/app/tabs/info/ServerFtp.svelte";
	import Parameters from "../../components/app/tabs/info/Parameters.svelte";
	import { ServerSubscriptionStatus, ServerTab } from "../../components/app/constants";
	import Pending from "../../components/app/tabs/info/Pending.svelte";
	import type { Unsubscriber } from "svelte/store";
	import SubscriptionInfo from "../../components/app/tabs/sub/SubscriptionInfo.svelte";
	import { getServerInfo } from "../../components/app/helpers";
	import NotFound from "../../components/app/tabs/info/NotFound.svelte";

	// State
	let server: DetailedServer = null;
	let error: string | null = null;
	let debug: boolean = false;
	let fetching: boolean = false;
	let unsubscribe: Unsubscriber | null = null;
	let isPending: boolean = true;
	let containerNotFound: boolean = false;

	onMount(async () => {
		if (!$token) {
			await goto("/login?redirect=/app");
		}

		unsubscribe = refreshServerInfo.subscribe(() => fetchInfo());
	});

	onDestroy(async () => {
		unsubscribe?.();
	});

	async function fetchInfo(value: Server = $selectedServer) {
		if (!value) return;

		try {
			error = null;
			fetching = true;
			server = null;
			server = await getServerInfo(value.id);
			isPending = server.subscriptionStatus === ServerSubscriptionStatus.PENDING;
			containerNotFound = server.subscriptionStatus === ServerSubscriptionStatus.ACTIVE && !server.serverCreated;
		} catch (err) {
			server = null;
			error = err;
		} finally {
			fetching = false;
		}
	}

	const toggleDebug = () => {
		debug = !debug;
	};

	async function changeTab(newTab: ServerTab) {
		$selectedTab = newTab;
	}

	$: fetchInfo($selectedServer);
</script>

<svelte:head>
	<title>Mes serveurs</title>
</svelte:head>

<style lang="scss">
	@include media-breakpoint-down(sm) {
		.content {
			margin-left: 74px;
		}
	}
</style>

<div class="d-flex vh-100">
	<Sidebar/>

	<div class="content overflow-auto flex-grow-1 p-3 d-flex flex-column gap-3">
		{#if $selectedServer}
			<ServerBar {server}/>

			<nav class="nav nav-pills nav-fill flex-column flex-lg-row">
				<button
						class="nav-link"
						class:active={$selectedTab === ServerTab.INFO}
						on:click|preventDefault={() => changeTab(ServerTab.INFO)}
				>
					Informations
				</button>

				<button
						class="nav-link"
						class:active={$selectedTab === ServerTab.CONSOLE}
						on:click|preventDefault={() => changeTab(ServerTab.CONSOLE)}
						disabled="{isPending}"
				>
					Console
				</button>

				<button
						class="nav-link"
						class:active={$selectedTab === ServerTab.SUBSCRIPTION}
						on:click|preventDefault={() => changeTab(ServerTab.SUBSCRIPTION)}
				>
					Abonnement
				</button>
			</nav>

			{#if $selectedTab === ServerTab.INFO}
				{#if isPending}
					<Pending/>
				{:else if containerNotFound}
					<NotFound/>
				{:else}
					<div class="d-flex gap-3 flex-column flex-xl-row">
						<ServerInfo {server}/>
						<ServerFtp {server}/>
					</div>

					{#if server}
						<Parameters parameters={server.parameters}/>
					{/if}
				{/if}
			{:else if $selectedTab === ServerTab.SUBSCRIPTION}
				<SubscriptionInfo/>
			{/if}
		{:else}
			<NoServer/>
		{/if}

		<hr>
		<button type="button" class="btn btn-sm btn-light" on:click|preventDefault={toggleDebug}>Debug</button>
		<pre class="collapse" class:show={debug}>{JSON.stringify(server, " ", 4)}</pre>
	</div>
</div>