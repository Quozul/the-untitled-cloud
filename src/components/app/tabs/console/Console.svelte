<script lang="ts">
	import { server, token } from "$store/store";
	import { onDestroy } from "svelte";
	import Icon from "$components/icons/Icon.svelte";
	import { parse } from "ansicolor";

	let socket: WebSocket;
	let logs: string = "";
	let input: HTMLElement;
	let consoleElement: HTMLElement;
	const encoder = new TextEncoder();

	function connect() {
		socket?.close();

		socket = new WebSocket(`${import.meta.env.VITE_API_WS_URL}server/${$server.id}/console`);

		socket.onopen = function() {
			socket.send(`Bearer ${$token}`); // Send authentication token
		};

		socket.onmessage = async function(event: MessageEvent) {
			const blob = event.data as Blob;
			const text = await blob.text();

			const parsed = parse(text);
			for (const element of parsed) {
				console.log(element);
				if (element.css) {
					logs += `<span style="${element.css}">${element.text}</span>`;
				} else {
					logs += element.text;
				}
			}
			consoleElement.scrollTo(0, consoleElement.scrollHeight);
		};

		socket.onclose = function() {
			logs = "";
			input.innerText = "";
			socket = null;
		};
	}

	function close() {
		socket?.close();
	}

	function submitCommand(event: KeyboardEvent) {
		if (event.key === "Enter") {
			const uint8Array = encoder.encode(input.textContent + "\n");
			socket?.send(uint8Array);
			input.innerText = "";
			event.preventDefault();
		}
	}

	onDestroy(close);
</script>

<style lang="scss">
	.console-container {
		display: flex;
		flex-direction: column;
		overflow-y: auto;
		overflow-x: hidden;

		.align {
			flex-grow: 1;
		}

		.console {
			display: inline-block;
			position: relative;
			font-family: monospace, sans-serif;
			white-space: pre-wrap;
			word-break: break-all;

			.input {
				display: inline-block;
				border: none;
				background: transparent;
				outline: none;
				color: white;
				cursor: text;
				height: 1em;
				word-break: break-all;

				&:empty:after {
					color: #777;
					content: "Entrez une commande...";
				}

				/*&:before {
					position: absolute;
					top: 0;
					left: 0;
					width: 100%;
					height: 100%;
					content: " ";
				}*/
			}
		}
	}
</style>

{#if !socket}
	<div class="d-flex justify-content-center align-items-center"><button class="btn btn-secondary btn-sm" on:click={connect}>Se connecter à la console</button></div>
{:else}
	<div class="console-container bg-dark text-white p-3 flex-grow-1">
		<div class="align"></div>
		<div class="console" bind:this={consoleElement}>
			{@html logs}
			<div contentEditable class="input" bind:this={input} on:keypress={submitCommand}></div>
		</div>
	</div>

	<div class="alert alert-info">
		<h6 class="fw-bold d-flex align-items-center gap-2">
			<Icon key="question"/>
			Pourquoi je ne vois pas les logs dans la console ?
		</h6>
		Il s'agit d'une console de contrôle à distance, elle permet simplement d'exécuter des commandes sur le serveur.<br/>
		Si vous souhaitez voir les logs, je vous invite à utiliser le FTP pour lire les fichiers de logs.
	</div>
{/if}