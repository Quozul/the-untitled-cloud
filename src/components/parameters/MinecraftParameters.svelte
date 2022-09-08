<script lang="ts">
	import ServerType from "./ServerType.svelte";
	import { parameters, server } from "$store/store";
	import { getParameters, putService } from "./helpers";
	import Button from "$shared/Button.svelte";
	import { putParameters } from "./helpers.js";
	import { patchServer, refreshAllServers, refreshSelectedServer } from "$components/app/helpers";
	import { Variant } from "$shared/constants";
	import AdvancedParameters from "./AdvancedParameters.svelte";
	import Icon from "$components/icons/Icon.svelte";
	import type { ApiService } from "$models/ApiService";

	$: fetchParameters($server)

	async function fetchParameters(s: ApiService = $server) {
		$parameters = await getParameters(s.id);
	}

	async function saveParameters() {
		await putParameters($server.id, $parameters);
		await putService($server, $server.name, $server.tag);
	}

	async function fullRefresh() {
		fetchParameters();
		refreshAllServers();
		refreshSelectedServer();
	}

	async function handleSave() {
		await saveParameters();
		await fullRefresh();
	}

	async function handleApply() {
		await saveParameters();
		await patchServer($server, "RECREATE");
		await fullRefresh();
	}

	async function handleRandomName() {
		await putService($server, null, $server.tag);

		// Perform global refresh
		await fullRefresh();
	}
</script>

<div class="bg-light p-4 d-flex element flex-column gap-3">
	<h4 class="mb-0">Paramètres du serveur Minecraft</h4>
	{#if $parameters && $server}
		<div>
			<label for="serverName" class="form-label">Nom du serveur</label>
			<div class="input-group">
				<input id="serverName" maxlength="32" class="form-control" placeholder={$server?.name} bind:value={$server.name}>
				<button class="btn btn-outline-secondary d-flex align-items-center gap-2" type="button" on:click={handleRandomName}>
					<Icon key="shuffle"/>
					Nom aléatoire
				</button>
			</div>
		</div>
	{/if}

	<div>
		<label for="tag" class="form-label">Environnement</label>
		<select class="form-select" id="tag" bind:value={$server.tag}>
			<option value="latest">Dernière</option>
			<option value="java17">Java 17</option>
			<option value="java17-graalvm-ce">GraalVM 17</option>
			<option value="java11">Java 11</option>
			<option value="java8-multiarch">Java 8</option>
		</select>
	</div>

	<h4 class="mb-0">Paramètres du serveur</h4>
	<div class="d-flex align-items-stretch gap-3">
		{#if $parameters}
			<ServerType/>
			<AdvancedParameters/>
		{/if}
	</div>

	<div class="d-flex gap-3 mt-3">
		<Button onClick={handleSave}>
			Sauvegarder
		</Button>

		<Button onClick={handleApply}>
			Sauvegarder et appliquer les changements
		</Button>

		<Button variant={Variant.SECONDARY} onClick={fetchParameters}>
			Annuler les changements
		</Button>
	</div>
</div>