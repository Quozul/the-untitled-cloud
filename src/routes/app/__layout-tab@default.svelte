<script lang="ts">
	import { selectedServer, server, fetchServersError } from "$store/store";
	import NoServer from "$components/app/tabs/info/errors/NoServer.svelte";
	import ServerBar from "$components/app/ServerBar.svelte";
	import { refreshAllServers } from "$components/app/helpers";
	import Tabs from "$components/app/Tabs.svelte";
	import InternalError from "$components/app/tabs/info/errors/InternalError.svelte";

	// State
	let showDebug: boolean = false;

	const toggleDebug = () => {
		showDebug = !showDebug;
	};
</script>

{#if $selectedServer}
	<ServerBar/>

	<Tabs/>

	<slot/>
{:else if $fetchServersError}
	<InternalError refresh={refreshAllServers}/>
{:else}
	<NoServer/>
{/if}

<hr>
<button type="button" class="btn btn-sm btn-light" on:click|preventDefault={toggleDebug}>Debug</button>
<pre class="collapse" class:show={showDebug}>{JSON.stringify($server, " ", 4)}</pre>