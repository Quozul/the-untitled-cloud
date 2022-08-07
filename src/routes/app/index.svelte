<script lang="ts">
	import Sidebar from "$components/app/sidebar/Sidebar.svelte";
	import { token, selectedServer, selectedTab, server, fetchServersError } from "$store/store";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import NoServer from "$components/app/tabs/info/errors/NoServer.svelte";
	import ServerBar from "$components/app/ServerBar.svelte";
	import { ServerTab } from "$components/app/constants";
	import SubscriptionInfo from "$components/app/tabs/sub/SubscriptionInfo.svelte";
	import { refreshSelectedServer, refreshAllServers } from "$components/app/helpers";
	import Tabs from "$components/app/Tabs.svelte";
	import InfoTab from "$components/app/InfoTab.svelte";
	import Console from "$components/app/tabs/console/Console.svelte";
	import InternalError from "$components/app/tabs/info/errors/InternalError.svelte";

	// State
	let showDebug: boolean = false;

	onMount(async () => {
		if (!$token) {
			await goto("/login?redirect=/app");
		}

		await refreshSelectedServer();
	});

	const toggleDebug = () => {
		showDebug = !showDebug;
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
		{#if $selectedServer}
			<ServerBar/>

			<Tabs/>

			{#if $selectedTab === ServerTab.INFO}
				<InfoTab/>
			{:else if $selectedTab === ServerTab.CONSOLE}
				<Console/>
			{:else if $selectedTab === ServerTab.SUBSCRIPTION}
				<SubscriptionInfo/>
			{/if}
		{:else if $fetchServersError}
			<InternalError refresh={refreshAllServers}/>
		{:else}
			<NoServer/>
		{/if}

		<hr>
		<button type="button" class="btn btn-sm btn-light" on:click|preventDefault={toggleDebug}>Debug</button>
		<pre class="collapse" class:show={showDebug}>{JSON.stringify($server, " ", 4)}</pre>
	</div>
</div>