<script lang="ts">
	import { server, fetchServersError } from "$store/store";
	import NoServer from "$components/errors/NoServer.svelte";
	import ServerBar from "$components/app/ServerBar.svelte";
	import { refreshAllServers } from "$components/app/helpers";
	import Tabs from "$components/app/Tabs.svelte";
	import InternalError from "$components/errors/InternalError.svelte";
</script>

<div class="order-0 order-lg-0 px-lg-3 pt-lg-3">
	<ServerBar />
</div>

<div class="flex-grow-1 overflow-auto order-1 order-lg-2 px-lg-3">
	{#if $server}
		<slot/>
	{:else if $fetchServersError}
		<InternalError refresh={refreshAllServers} />
	{:else}
		<NoServer />
	{/if}
</div>

<div class="order-2 order-lg-1 order-last px-lg-3">
	<Tabs />
</div>