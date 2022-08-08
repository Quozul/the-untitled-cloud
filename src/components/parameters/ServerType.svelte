<script lang="ts">
	import { ServerType } from "$components/app/constants";
	import { parameters } from "$store/store";
	import Select from "$components/select/Select.svelte";
	import Option from "$components/select/Option.svelte";
	import { capitalize } from "$shared/helpers";
	import { Versions } from "./constants/versions.js";
	import { t } from "svelte-intl-precompile";

	let defaultVersionName: string;
	$: if ($parameters.version === "latest" || $parameters.version === "snapshot") {
		defaultVersionName = $t(`${$parameters.version}_version`);
	} else {
		defaultVersionName = $parameters.version;
	}
</script>

<div class="d-flex flex-column gap-3 justify-content-start">
	<div class="d-flex gap-3 justify-content-start">
		<div>
			<label class="form-label">Type de serveur</label>
			<Select bind:value={$parameters.serverType} defaultText={capitalize($parameters.serverType)} placeholder="Type de serveur">
				{#each Object.keys(ServerType) as type}
					<Option text={capitalize(type)} value={type}/>
				{/each}
			</Select>
		</div>

		<div>
			<label class="form-label">Version de Minecraft</label>
			<Select bind:value={$parameters.version} defaultText={defaultVersionName} placeholder="Version de Minecraft">
				<Option text="Dernière" value="latest"/>
				<Option text="Snapshot" value="snapshot"/>

				{#each Versions as version}
					<Option text={version} value={version}/>
				{/each}
			</Select>
		</div>
	</div>

	<div class="d-flex gap-3 justify-content-start">
		{#if $parameters.serverType === ServerType.FORGE}
			<div>
				<label for="forgeVersion" class="form-label">Version de Forge</label>
				<input id="forgeVersion" class="form-control" placeholder="41.1.0" bind:value={$parameters.forgeVersion}>
			</div>
		{:else if $parameters.serverType === ServerType.FABRIC}
			<div>
				<label for="fabricLauncherVersion" class="form-label">Version du lanceur de Fabric</label>
				<input id="fabricLauncherVersion" class="form-control" placeholder="0.11.0" bind:value={$parameters.fabricLauncherVersion}>
			</div>

			<div>
				<label for="fabricLoaderVersion" class="form-label">Version de Fabric</label>
				<input id="fabricLoaderVersion" class="form-control" placeholder="0.13.1" bind:value={$parameters.fabricLoaderVersion}>
			</div>
		{:else if $parameters.serverType === ServerType.QUILT}
			<div>
				<label for="quiltLauncherVersion" class="form-label">Version du lanceur de Quilt</label>
				<input id="quiltLauncherVersion" class="form-control" placeholder="0.16.0" bind:value={$parameters.quiltLauncherVersion}>
			</div>

			<div>
				<label for="quiltLoaderVersion" class="form-label">Version de Quilt</label>
				<input id="quiltLoaderVersion" class="form-control" placeholder="0.4.1" bind:value={$parameters.quiltLoaderVersion}>
			</div>
		{:else if $parameters.serverType === ServerType.FTBA}
			<div>
				<label for="ftbModpackId" class="form-label">Id du modpack Feed The Beast</label>
				<input id="ftbModpackId" class="form-control" placeholder="35" bind:value={$parameters.ftbModpackId}>
			</div>

			<div>
				<label for="ftpModpackVersionId" class="form-label">Version du modpack</label>
				<input id="ftpModpackVersionId" class="form-control" placeholder="2129" bind:value={$parameters.ftbModpackVersionId}>
			</div>
		{/if}
	</div>
</div>