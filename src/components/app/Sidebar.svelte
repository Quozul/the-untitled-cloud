<script lang="ts">
	import { onMount } from "svelte";
	import { selectedServer, token } from "../../store/store.js";
	import type { Paginate, Server } from "./models";
	import Icon from "../icons/Icon.svelte";
	import { containId, getAllServers } from "../../shared/helpers";
	import { goto } from "$app/navigation";

	let servers: Paginate<Server>;
	let currentPage = 0;
	let fetching = true;

	onMount(async () => {
		try {
			servers = await getAllServers(currentPage);

			if (servers.data.length > 0 && (!!$selectedServer || !containId(servers, $selectedServer.id))) {
				$selectedServer = servers.data[0];
			} else {
				$selectedServer = null;
			}
		} finally {
			fetching = false;
		}
	});

	async function logout() {
		$token = null;
		await goto("/");
	}
</script>

<div class="d-flex flex-column flex-shrink-0 p-3 bg-light" style="width: 280px;">
    <a href="/" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none">
        <Icon key="box" width="32" height="32"/>
        <span class="fs-4">Quozul.cloud</span>
    </a>

    <hr>

    <div class="mb-auto">
        {#if fetching}
            <button class="btn btn-secondary w-100 placeholder" disabled></button>
        {/if}

        {#if servers && servers.data.length > 0}
            <ul class="nav nav-pills flex-column gap-3">
                {#each servers.data as server}
                    <button
                            class="btn btn-outline-primary d-flex align-items-center w-100"
                            class:active={$selectedServer.id === server.id}
                            class:btn-outline-danger={!server.serverStatus}
                            on:click={() => $selectedServer = server}
                    >
                        {#if !server.serverStatus}
                            <Icon key="warning" className="me-2"/>
                        {:else}
                            <Icon key="box" className="me-2"/>
                        {/if}

                        {server.name ?? "Serveur introuvable"}
                    </button>
                {/each}
            </ul>

            <hr/>
        {/if}

        <a href="/rent/products/" class="btn text-start btn-outline-secondary d-flex align-items-center w-100">
            <Icon key="plus" className="me-2"/>
            Louer un serveur
        </a>
    </div>

    <hr/>

    <ul class="nav flex-column gap-3">
        <a class="btn btn-outline-dark d-flex align-items-center w-100" href="/">
            <Icon key="chevron-left" className="me-2"/>
            Accueil
        </a>
        <button class="btn btn-outline-dark d-flex align-items-center w-100" on:click|preventDefault={logout}>
            <Icon key="box-arrow-left" className="me-2"/>
            Se déconnecter
        </button>
    </ul>
</div>