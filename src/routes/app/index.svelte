<script context="module">
	export const prerender = false;
</script>

<script lang="ts">
	import ServerInfo from "../../components/app/tabs/info/ServerInfo.svelte";
	import Sidebar from "../../components/app/sidebar/Sidebar.svelte";
	import { token, selectedServer, refreshServer } from "../../store/store";
	import { goto } from "$app/navigation";
	import { onDestroy, onMount } from "svelte";
	import NoServer from "../../components/app/tabs/info/NoServer.svelte";
	import type { DetailedServer, Server } from "../../components/app/models.js";
	import { getServerInfo } from "../../shared/helpers.js";
	import ServerBar from "../../components/app/ServerBar.svelte";
	import ServerFtp from "../../components/app/tabs/info/ServerFtp.svelte";
	import Parameters from "../../components/app/tabs/info/Parameters.svelte";
	import { ServerSubscriptionStatus, ServerTab } from "../../components/app/constants.js";
	import Pending from "../../components/app/tabs/info/Pending.svelte";
	import type { Unsubscriber } from "svelte/store";

	// State
	let server: DetailedServer = null;
	let error: string | null = null;
	let debug: boolean = false;
	let fetching: boolean = false;
	let unsubscribe: Unsubscriber | null = null;
	let tab: ServerTab = ServerTab.INFO;
	let isPending: boolean = true;

	onMount(async () => {
		if (!$token) {
			await goto("/login?redirect=/app");
		}

		unsubscribe = refreshServer.subscribe(() => fetchInfo());
	});

	onDestroy(async () => {
		unsubscribe?.();
	});

	async function fetchInfo(value: Server = $selectedServer) {
		if (!value) return;
		try {
			error = null;
			fetching = true;
			server = await getServerInfo(value.id);
			isPending = server.subscriptionStatus === ServerSubscriptionStatus.PENDING;
		} catch (err) {
			server = null;
			error = err;
		} finally {
			fetching = false;
		}
	}

	$: fetchInfo($selectedServer);

	const toggleDebug = () => {
		debug = !debug;
	};

	const changeTab = (newTab: ServerTab) => {
		tab = newTab;
	};
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
		{#if $selectedServer && server}
			<ServerBar {server}/>

			<nav class="nav nav-pills nav-fill flex-column flex-lg-row">
				<button
						class="nav-link"
						class:active={tab === ServerTab.INFO}
						on:click|preventDefault={() => changeTab(ServerTab.INFO)}
				>
					Informations
				</button>
				<button
						class="nav-link"
						class:active={tab === ServerTab.SUBSCRIPTION}
						on:click|preventDefault={() => changeTab(ServerTab.SUBSCRIPTION)}
				>
					Abonnement
				</button>
				<button
						class="nav-link"
						class:active={tab === ServerTab.CONSOLE}
						on:click|preventDefault={() => changeTab(ServerTab.CONSOLE)}
						disabled="{isPending}"
				>
					Console
				</button>
			</nav>

			{#if tab === ServerTab.INFO}
				{#if isPending}
					<Pending/>
				{:else}
					<div class="d-flex gap-3 flex-column flex-xl-row">
						{#if server.serverCreated}
							<ServerInfo {server}/>
							<ServerFtp {server}/>
						{/if}
					</div>

					<Parameters parameters={server.parameters}/>
				{/if}
			{:else if tab === ServerTab.SUBSCRIPTION}
				Sub
			{/if}
		{:else}
			<NoServer/>
		{/if}

		<hr>
		<button type="button" class="btn btn-sm btn-light" on:click|preventDefault={toggleDebug}>Debug</button>
		<pre class="collapse" class:show={debug}>{JSON.stringify(server, " ", 4)}</pre>
	</div>
</div>