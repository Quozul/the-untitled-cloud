<script lang="ts">
	import { selectedServer, token } from "$store/store";
	import { onDestroy } from "svelte";
	import type { DetailedServer } from "$components/app/models";

	export let server: DetailedServer;

	let socket: WebSocket;
	let logs: string = "";
	let command: string = "";
	let consoleElement: HTMLElement;

	const isStopped: boolean = !server?.state?.running;

	function connect() {
		socket?.close();

		socket = new WebSocket(`ws://localhost:8080/server/${$selectedServer.id}/console`);

		socket.onopen = function() {
			socket.send(`Bearer ${$token}`); // Send authentication token
		};

		socket.onmessage = function(event: MessageEvent) {
			logs += event.data;
			consoleElement.scrollTo(0, consoleElement.scrollHeight);
		};

		socket.onclose = function() {
			logs = "";
			command = "";
			socket = null;
		};
	}

	function close() {
		socket?.close();
	}

	function submitCommand() {
		socket?.send(command);
		command = "";
	}

	onDestroy(close);
</script>

<pre class="text-white bg-dark p-3 mb-0" bind:this={consoleElement}>
{#if socket}
{logs}
{:else}<div class="d-flex justify-content-center align-items-center"><button class="btn btn-secondary btn-sm" on:click={connect}>Se connecter à la console</button></div>{/if}</pre>
<form class="input-group">
	<input type="text" class="form-control bg-dark border-0 text-white" placeholder=">" bind:value={command} disabled="{isStopped}">
	<button on:click|preventDefault={submitCommand} class="btn btn-dark" type="submit" disabled="{isStopped}">Executer</button>
</form>
{#if isStopped}
	Vous ne pouvez pas exécuter de commandes lorsque le serveur est éteint.
{/if}