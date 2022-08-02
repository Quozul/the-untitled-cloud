<script lang="ts">
	import { onMount } from "svelte";
	import { fetchedServers, selectedServer } from "../../store/store.js";
	import type { Paginate, Server } from "./models";
	import Icon from "../icons/Icon.svelte";
	import { containId, getAllServers } from "../../shared/helpers";

	let servers: Paginate<Server>;
	let currentPage = 0;

	onMount(async () => {
		$fetchedServers = false;
		servers = await getAllServers(currentPage);

		if (!!$selectedServer || !containId(servers, $selectedServer)) {
			$selectedServer = servers.data[0]?.id ?? null;
		}

		$fetchedServers = true;
	});
</script>

<div class="d-flex flex-column flex-shrink-0 p-3 bg-light" style="width: 280px;">
    <a href="/" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none">
        <svg class="bi pe-none me-2" width="32" height="32">
            <use xlink:href="#box"></use>
        </svg>
        <span class="fs-4">Quozul.host</span>
    </a>
    <hr>

    <ul class="nav nav-pills flex-column mb-auto gap-3">
        {#if servers}
            {#each servers.data as server}
                <li class="nav-item">
                    <button class="nav-link d-flex align-items-center w-100" class:active={$selectedServer === server.id} on:click={() => $selectedServer = server.id}>
                        <Icon key="box" className="me-2"/>
                        {server.name}
                    </button>
                </li>
            {/each}
        {/if}
        <li class="nav-item">
            <a href="/checkout" class="btn btn-outline-primary w-100">
                <Icon key="plus" className="me-2"/>
            </a>
        </li>
    </ul>
</div>