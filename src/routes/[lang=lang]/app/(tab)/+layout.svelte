<script lang="ts">
	import { server, fetchServersError } from "$store/store";
	import NoServer from "$components/errors/NoServer.svelte";
	import ServerBar from "$components/app/ServerBar.svelte";
	import { refreshAllServers } from "$components/app/helpers";
	import Tabs from "$components/app/Tabs.svelte";
	import InternalError from "$components/errors/InternalError.svelte";
	import { fetchingServer } from "$store/store";
</script>

{#if $server}
	<ServerBar />
	<Tabs />
	<slot />
{:else if $fetchServersError}
	<InternalError refresh={refreshAllServers} />
{:else if $fetchingServer}
	<ServerBar />
	<Tabs />
{:else}
	<NoServer />
{/if}
