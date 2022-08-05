<script lang="ts">
	import type { ServerParameters, Version } from "$components/app/models";
	import { ServerType } from "$components/app/constants";
	import { onMount } from "svelte";
	import { VersionType } from "$components/app/models";
    import { refreshServerInfo, selectedServer } from "$store/store";
	import Button from "$shared/Button.svelte";
    import { putParameters, toggleRefreshServerInfo } from "$components/app/helpers";

	export let parameters: ServerParameters;
	let versions: Version[] = [];

	onMount(() => {
		// TODO: This will be called everytime a change to serverType or jdkVersion is made
		fetch(`${import.meta.env.VITE_API_BASE_URL}versions`, { method: "GET" })
            .then(res => res.json())
            .then((json: Version[]) => {
				versions = json;
            });
    })

	async function submit() {
		await putParameters($selectedServer.id, parameters);
        toggleRefreshServerInfo();
	}
</script>

<form class="bg-light p-4 d-flex element flex-column">
    <h4>Paramètres</h4>

    <div class="form-check">
        <input class="form-check-input" type="checkbox" id="eula" name="eula" bind:checked={parameters.eula}>
        <label class="form-check-label" for="eula">
            EULA
        </label>
    </div>

    <div class="mb-3">
        <label for="serverType" class="form-label">Type de serveur</label>
        <select id="serverType" class="form-select" bind:value={parameters.serverType} required>
            {#each Object.keys(ServerType) as type}
                <option value="{type}">{type}</option>
            {/each}
        </select>
    </div>

    <div class="mb-3">
        <label for="version" class="form-label">Version</label>
        <select id="version" class="form-select" bind:value={parameters.version} required>
            {#each versions as version}
                {#if parameters.serverType === ServerType.VANILLA && version.type === VersionType.SNAPSHOT || version.type === VersionType.RELEASE}
                    <option value="{version.id}">{version.id}</option>
                {/if}
            {/each}
        </select>
    </div>

    {#if parameters.serverType === ServerType.FORGE}
        <div class="mb-3">
            <label for="forgeVersion" class="form-label">Version de Forge</label>
            <input id="forgeVersion" class="form-control" placeholder="14.23.5.2854" bind:value={parameters.forgeVersion}>
        </div>
    {:else if parameters.serverType === ServerType.FABRIC}
        <div class="mb-3">
            <label for="fabricLauncherVersion" class="form-label">Version du lanceur de Fabric</label>
            <input id="fabricLauncherVersion" class="form-control" placeholder="0.10.2" bind:value={parameters.fabricLauncherVersion}>
        </div>

        <div class="mb-3">
            <label for="fabricLoaderVersion" class="form-label">Version de Fabric</label>
            <input id="fabricLoaderVersion" class="form-control" placeholder="0.13.1" bind:value={parameters.fabricLoaderVersion}>
        </div>
    {:else if parameters.serverType === ServerType.QUILT}
        <div class="mb-3">
            <label for="quiltLauncherVersion" class="form-label">Version du lanceur de Quilt</label>
            <input id="quiltLauncherVersion" class="form-control" placeholder="0.16.0" bind:value={parameters.quiltLauncherVersion}>
        </div>

        <div class="mb-3">
            <label for="quiltLoaderVersion" class="form-label">Version de Quilt</label>
            <input id="quiltLoaderVersion" class="form-control" placeholder="0.4.1" bind:value={parameters.quiltLoaderVersion}>
        </div>
    {:else if parameters.serverType === ServerType.FTBA}
        <div class="mb-3">
            <label for="ftbModpackId" class="form-label">Id du modpack</label>
            <input id="ftbModpackId" class="form-control" placeholder="35" bind:value={parameters.ftbModpackId}>
        </div>

        <div class="mb-3">
            <label for="ftpModpackVersionId" class="form-label">Version du modpack</label>
            <input id="ftpModpackVersionId" class="form-control" placeholder="2129" bind:value={parameters.ftbModpackVersionId}>
        </div>
    {/if}

    <!-- OVERRIDE_SERVER_PROPERTIES=false -->

    <Button className="btn btn-primary" onClick={submit}>
        Sauvegarder
    </Button>
</form>