<script lang="ts">
	import { token } from "../../store/store.ts";
	import { onMount } from "svelte";
	import { selectedServer } from "../../store/store.js";
	import type { Paginate, Server } from "./models";
	import Icon from "../icons/Icon.svelte";

	let servers: Paginate<Server>;
	let error = false;
	let currentPage = 0;

	onMount(async () => {
		fetch(`${ import.meta.env.VITE_API_BASE_URL }/server?page=${ currentPage }`, {
			method: "GET",
			headers: new Headers({ "authorization": `Bearer ${ $token }` }),
		})
			.then(res => {
				error = !res.ok;
				return res.json();
			})
			.then(json => {
				servers = json;
				if (!!$selectedServer) {
					$selectedServer = servers.data[0]?.id ?? null;
                }
			});
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

    <ul class="nav nav-pills flex-column mb-auto">
        {#if servers}
            {#each servers.data as server}
                <li class="nav-item">
                    <button class="nav-link d-flex align-items-center" class:active={$selectedServer === server.id} on:click={() => $selectedServer = server.id}>
                        <Icon key="box" className="me-2"/>
                        {server.name}
                    </button>
                </li>
            {/each}
        {/if}
    </ul>
</div>