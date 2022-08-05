<script lang="ts">
	import Sidebar from "$components/app/sidebar/Sidebar.svelte";
	import { token, selectedServer, selectedTab, refreshServerInfo } from "$store/store";
	import { goto } from "$app/navigation";
	import { onDestroy, onMount } from "svelte";
	import NoServer from "../../components/app/tabs/info/errors/NoServer.svelte";
	import type { DetailedServer, Server } from "$components/app/models";
	import ServerBar from "$components/app/ServerBar.svelte";
	import { ServerTab } from "$components/app/constants";
	import type { Unsubscriber } from "svelte/store";
	import SubscriptionInfo from "$components/app/tabs/sub/SubscriptionInfo.svelte";
	import { getServerInfo } from "$components/app/helpers";
	import Tabs from "$components/app/Tabs.svelte";
	import InfoTab from "$components/app/InfoTab.svelte";

	// State
	let server: DetailedServer = null;
	let error: string | null = null;
	let debug: boolean = false;
	let fetching: boolean = false;
	let unsubscribe: Unsubscriber | null = null;

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

			<Tabs {server}/>

			{#if $selectedTab === ServerTab.INFO}
				<InfoTab {server}/>
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