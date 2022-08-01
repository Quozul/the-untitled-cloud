<script lang="ts">
	import type { ServerParameters } from "./models";
	import { ServerType } from "./constants.js";
	import { selectedServer, token } from "../../store/store";

	export let parameters: ServerParameters;

	function submit() {
		fetch(`${import.meta.env.VITE_API_BASE_URL}/server/${$selectedServer}/parameters`, {
			method: "PUT",
			headers: new Headers({
				"content-type": "application/json",
				"authorization": `Bearer ${$token}`,
			}),
			body: JSON.stringify(parameters),
		});
	}
</script>

<form>
    <h4>Paramètres</h4>

    <div class="form-check">
        <input class="form-check-input" type="checkbox" id="eula" name="eula" bind:checked={parameters.eula}>
        <label class="form-check-label" for="eula">
            EULA
        </label>
    </div>

    <div class="mb-3">
        <label for="serverType" class="form-label">Type de serveur</label>
        <select id="serverType" class="form-select" bind:value={parameters.serverType}>
            {#each Object.keys(ServerType) as type}
                <option value="{type}">{type}</option>
            {/each}
        </select>
    </div>

    <div class="mb-3">
        <label for="version" class="form-label">Version</label>
        <select id="version" class="form-select">
            <option selected>Open this select menu</option>
            <option value="latest">Dernière</option>
            <option value="snapshot">Snapshot</option>
        </select>
    </div>

    {#if parameters.serverType === ServerType.FORGE}
        <div class="mb-3">
            <label for="forgeVersion" class="form-label">Version de Forge</label>
            <input id="forgeVersion" class="form-control" placeholder="14.23.5.2854">
        </div>
    {:else if parameters.serverType === ServerType.FABRIC}
        <div class="mb-3">
            <label for="fabricLauncherVersion" class="form-label">Version du lanceur de Fabric</label>
            <input id="fabricLauncherVersion" class="form-control" placeholder="0.10.2">
        </div>

        <div class="mb-3">
            <label for="fabricLoaderVersion" class="form-label">Version de Fabric</label>
            <input id="fabricLoaderVersion" class="form-control" placeholder="0.13.1">
        </div>
    {:else if parameters.serverType === ServerType.QUILT}
        <div class="mb-3">
            <label for="quiltLauncherVersion" class="form-label">Version du lanceur de Quilt</label>
            <input id="quiltLauncherVersion" class="form-control" placeholder="0.16.0">
        </div>

        <div class="mb-3">
            <label for="quiltLoaderVersion" class="form-label">Version de Quilt</label>
            <input id="quiltLoaderVersion" class="form-control" placeholder="0.4.1">
        </div>
    {:else if parameters.serverType === ServerType.FTBA}
        <div class="mb-3">
            <label for="ftbModpackId" class="form-label">Id du modpack</label>
            <input id="ftbModpackId" class="form-control" placeholder="35_ftb_revelation">
        </div>

        <div class="mb-3">
            <label for="ftpModpackVersionId" class="form-label">Version du modpack</label>
            <input id="ftpModpackVersionId" class="form-control" placeholder="2129">
        </div>
    {/if}

    <!-- OVERRIDE_SERVER_PROPERTIES=false -->

    <button class="btn btn-primary" on:click|preventDefault={submit}>Sauvegarder</button>
</form>