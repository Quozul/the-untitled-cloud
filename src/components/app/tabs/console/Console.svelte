<script lang="ts">
	import { selectedServer, token } from "$store/store";
	import { onDestroy } from "svelte";

	let socket: WebSocket;
	let logs: string = "";
	let command: string = "";
	let consoleElement: HTMLElement;

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
		};
	}

	function close() {
		socket?.close();
		logs = "";
	}

	function submitCommand() {
		socket?.send(command);
		command = "";
	}

	onDestroy(close);
</script>

<div>
	<button class="btn btn-secondary btn-sm" on:click={connect}>Connect</button>
	<button class="btn btn-secondary btn-sm" on:click={close}>Close</button>
</div>

<pre class="text-white bg-dark p-3 mb-0" bind:this={consoleElement}>{logs}</pre>
<form class="input-group mb-3">
	<input type="text" class="form-control bg-dark border-0 text-white" placeholder=">" bind:value={command}>
	<button on:click|preventDefault={submitCommand} class="btn btn-dark" type="submit">Executer</button>
</form>
