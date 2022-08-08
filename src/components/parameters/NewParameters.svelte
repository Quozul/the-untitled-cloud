<script lang="ts">
	import ServerType from "./ServerType.svelte";
	import { parameters, selectedServer } from "$store/store";
	import { onDestroy, onMount } from "svelte";
	import { getParameters, putName } from "./helpers";
	import Button from "$shared/Button.svelte";
	import { putParameters } from "./helpers.js";
	import { patchServer, refreshAllServers, refreshSelectedServer } from "../app/helpers";
	import { ButtonVariant } from "$shared/constants";
	import AdvancedParameters from "./AdvancedParameters.svelte";
	import type { Unsubscriber } from "svelte/store";
	import type { Server } from "../app/models";
	import Icon from "../icons/Icon.svelte";

	let unsubscribe: Unsubscriber;

	onMount(async () => {
		selectedServer.subscribe(fetchParameters)
		await fetchParameters();
	});

	onDestroy(() => {
		unsubscribe?.();
	});

	async function fetchParameters(server: Server = $selectedServer) {
		$parameters = await getParameters(server.id);
	}

	async function saveParameters() {
		await putParameters($selectedServer.id, $parameters);

		// Perform refresh if name is different
		if ($parameters.name != $selectedServer.name) {
			await refreshAllServers();
			await refreshSelectedServer();
		}
	}

	async function saveAndApplyParameters() {
		await putParameters($selectedServer.id, $parameters);
		await patchServer($selectedServer.id, "RESTART");
	}

	async function setNameToNull() {
		await putName($selectedServer.id, null);

		// Perform global refresh
		await fetchParameters();
		await refreshAllServers();
		await refreshSelectedServer();
	}
</script>

<div class="bg-light p-4 d-flex element flex-column gap-3">
	<h4 class="mb-0">Paramètres</h4>

	{#if $parameters && $selectedServer}
		<div>
			<label for="serverName" class="form-label">Nom du serveur</label>
			<div class="input-group">
				<input id="serverName" maxlength="32" class="form-control" placeholder={$selectedServer?.name} bind:value={$parameters.name}>
				<button class="btn btn-outline-secondary d-flex align-items-center gap-2" type="button" on:click={setNameToNull}>
					<Icon key="shuffle"/>
					Nom aléatoire
				</button>
			</div>
		</div>
	{/if}

	<div class="d-flex align-items-stretch gap-3">
		{#if $parameters}
			<ServerType/>
			<AdvancedParameters/>
		{/if}
	</div>

	<div class="d-flex gap-3 mt-3">
		<Button onClick={saveParameters}>
			Sauvegarder
		</Button>

		<Button onClick={saveAndApplyParameters}>
			Sauvegarder et appliquer les changements
		</Button>

		<Button variant={ButtonVariant.SECONDARY} onClick={fetchParameters}>
			Annuler les changements
		</Button>
	</div>
</div>