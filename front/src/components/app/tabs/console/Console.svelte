<script lang="ts">
	import { server, token } from "$store/store";
	import { onDestroy } from "svelte";
	import { parse } from "ansicolor";
	import { t } from "svelte-intl-precompile";

	let socket: WebSocket;
	let logs = "";
	let input: HTMLElement;
	let consoleElement: HTMLElement;
	let submitting = false;

	const encoder = new TextEncoder();

	function connect() {
		socket?.close();

		socket = new WebSocket(`${import.meta.env.VITE_API_WS_URL}server/${$server.id}/console`);

		socket.onopen = function () {
			socket.send(`Bearer ${$token}`); // Send authentication token
		};

		socket.onmessage = async function (event: MessageEvent) {
			submitting = false;
			const blob = event.data as Blob;
			const text = await blob.text();

			// TODO: Stop using an external library
			const parsed = parse(text);
			for (const element of parsed) {
				if (element.css) {
					logs += `<span style="${element.css}">${element.text}</span>`;
				} else {
					logs += element.text;
				}
			}
			consoleElement.scrollTo(0, consoleElement.scrollHeight);
		};

		socket.onclose = function () {
			logs = "";
			socket = null;
			if (input) input.innerText = "";
		};
	}

	function close() {
		socket?.close();
	}

	function submitCommand(event: KeyboardEvent) {
		if (event.key === "Enter") {
			submitting = true;
			const uint8Array = encoder.encode(input.textContent + "\n");
			socket?.send(uint8Array);
			input.innerText = "";
			event.preventDefault();
		}
	}

	onDestroy(close);
</script>

{#if $server?.state?.starting}
	<div class="alert alert-info">{$t("console.server_is_starting")}</div>
{:else if !$server?.state?.running}
	<div class="alert alert-info">
		{$t("console.server_must_be_started")}
	</div>
{:else if !socket}
	<div class="d-flex justify-content-center align-items-center mt-2">
		<button class="btn btn-secondary" on:click={connect}>
			{$t("console.connect_to_console")}
		</button>
	</div>
{:else}
	<div class="console-container bg-dark text-white p-3 flex-grow-1">
		<div class="align" />
		<div class="console" bind:this={consoleElement}>
			{@html logs}
			<div contentEditable class="input" bind:this={input} on:keypress={submitCommand}>
				{#if submitting}
					<span class="spinner-border spinner-border-sm spinner" />
				{/if}
			</div>
		</div>
	</div>
{/if}

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

				.spinner {
					user-select: none;
				}
			}
		}
	}
</style>
