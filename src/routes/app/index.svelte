<script>
	import ServerInfo from "../../components/app/ServerInfo.svelte";
	import Sidebar from "../../components/app/Sidebar.svelte";
	import { token, selectedServer } from "../../store/store";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import { ServerStatus } from "../../components/app/constants";
	import Icon from "../../components/icons/Icon.svelte";
	import NoServer from "../../components/app/NoServer.svelte";

	onMount(async () => {
		if (!$token) {
			await goto("/login?redirect=/app");
		}
    })
</script>

<div class="d-flex vh-100">
    <Sidebar/>

    <div class="overflow-auto flex-grow-1 p-3">
        {#if $selectedServer}
            <h1
                class="d-flex align-items-center"
                class:text-success={$selectedServer.serverStatus === ServerStatus.RUNNING}
                class:text-danger={$selectedServer.serverStatus !== ServerStatus.RUNNING}
            >
                {#if $selectedServer.serverStatus === ServerStatus.RUNNING}
                    <Icon key="play" width="48" height="48"/>
                {:else}
                    <Icon key="stop" width="48" height="48"/>
                {/if}

                {$selectedServer.name}
            </h1>

            <nav class="nav nav-pills nav-fill">
                <a class="nav-link active" href="#">Informations</a>
                <a class="nav-link" href="#">Abonnement</a>
                <a class="nav-link" href="#">Console</a>
            </nav>

            <ServerInfo/>
        {:else}
            <NoServer/>
        {/if}
    </div>
</div>