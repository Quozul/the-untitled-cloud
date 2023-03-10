<script lang="ts">
	import ServerType from "./ServerType.svelte";
	import { parameters, server } from "$store/store";
	import { getParameters, putService } from "./helpers";
	import Button from "$shared/Button.svelte";
	import { putParameters } from "./helpers";
	import { patchServer, refreshAllServers, refreshSelectedServer } from "$components/app/helpers";
	import AdvancedParameters from "./AdvancedParameters.svelte";
	import Icon from "$components/icons/Icon.svelte";
	import type { ApiService } from "$models/ApiService";
	import { onDestroy, onMount } from "svelte";
	import type { Unsubscriber } from "svelte/store";
	import { subscribe } from "svelte/internal";
	import { t } from "svelte-intl-precompile";

	let edited = false;
	let unsubscribe: Unsubscriber | null = null;

	onMount(async () => {
		await fetchParameters();

		unsubscribe = subscribe(parameters, setEdited);
		edited = false;
	});

	onDestroy(() => {
		unsubscribe?.();
		$parameters = null;
	});

	function setEdited() {
		edited = true;
	}

	async function fetchParameters(s: ApiService = $server) {
		const { response } = await getParameters(s.id);
		$parameters = response;
		edited = false;
	}

	async function saveParameters() {
		await putParameters($server.id, $parameters);
		await putService($server, $server.name, $server.tag);
		edited = false;
	}

	async function fullRefresh() {
		await Promise.all([fetchParameters(), refreshAllServers(), refreshSelectedServer()]);
	}

	async function handleSave() {
		await saveParameters();
		await fullRefresh();
	}

	async function handleApply() {
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
	<h4 class="mb-0">{$t("tabs.parameters")}</h4>
	{#if $parameters && $server}
		<div>
			<label for="serverName" class="form-label">{$t("parameters.server_name")}</label>
			<div class="input-group">
				<input
					id="serverName"
					maxlength="32"
					class="form-control"
					placeholder={$server?.name}
					bind:value={$server.name}
					on:change={setEdited}
				/>
				<button
					class="btn btn-outline-secondary d-flex align-items-center gap-2"
					type="button"
					on:click={handleRandomName}
				>
					<Icon key="shuffle" />
					<span class="d-none d-sm-inline">{$t("parameters.random_name")}</span>
				</button>
			</div>
		</div>
	{/if}

	<div>
		<label for="tag" class="form-label">{$t("parameters.environment")}</label>
		<select class="form-select" id="tag" bind:value={$server.tag} on:change={setEdited}>
			<option value="latest">{$t("common.default")}</option>
			<option value="java17">Java 17</option>
			<option value="java17-graalvm-ce">GraalVM 17</option>
			<option value="java11">Java 11</option>
			<option value="java8-multiarch">Java 8</option>
		</select>
	</div>

	<div class="d-flex flex-column flex-lg-row align-items-stretch gap-3">
		{#if $parameters}
			<ServerType />
			<AdvancedParameters />
		{/if}
	</div>

	<div class="d-flex gap-3 mt-3">
		<Button onClick={handleSave} disabled={!edited} icon="save">{$t("common.save")}</Button>

		<Button onClick={handleApply} disabled={!edited} icon="arrow-clockwise">
			{$t("common.apply")}
		</Button>
	</div>
</div>
