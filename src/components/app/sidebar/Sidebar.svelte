<script lang="ts">
	import { onMount } from "svelte";
	import { selectedServer, sidebarCollapsed, token } from "../../../store/store.js";
	import type { Paginate, Server } from "../models";
	import Icon from "../../icons/Icon.svelte";
	import { containId, getAllServers } from "../../../shared/helpers";
	import { goto } from "$app/navigation";
	import SidebarItem from "./SidebarItem.svelte";
	import ServerItem from "./ServerItem.svelte";

	// State
	let servers: Paginate<Server>;
	let currentPage: number = 0;
	let fetching: boolean = true;

	onMount(async () => {
		try {
			servers = await getAllServers(currentPage);

			const hasSelectedServer: boolean = !!$selectedServer;
			const hasServers: boolean = servers.data.length > 0;
			const contains: boolean = hasSelectedServer && hasServers && containId(servers, $selectedServer?.id);

			if (!contains) {
				$selectedServer = servers.data[0];
			} else if (!hasServers) {
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

	const toggleCollapsed = () => {
		$sidebarCollapsed = !$sidebarCollapsed;
    }
</script>

<style lang="scss" global>
	@include media-breakpoint-down(sm) {
		.sidebar {
            position: absolute;
            left: 0;
            top: 0;
            height: 100%;
		}
	}

	.sidebar {
        z-index: 1000;
		width: 280px;
        transition: .2s width;

		&.collapsed {
			width: 74px;
		}

        .sidebar-item {
            height: 38px;
        }
	}
</style>

<div class="d-flex flex-column flex-shrink-0 p-3 bg-light sidebar shadow-sm" class:collapsed={$sidebarCollapsed}>
    <a href="/static" class="d-flex align-items-center me-md-auto link-dark text-decoration-none">
        <Icon key="box" width="42" height="38" className={!$sidebarCollapsed && "me-2"}/>
        {#if !$sidebarCollapsed}
            <span class="fs-4">Quozul.cloud</span>
        {/if}
    </a>

    <SidebarItem
        iconName={$sidebarCollapsed ? "chevron-double-right" : "chevron-double-left"}
        className="btn-outline-secondary mt-3 collapse-button"
        onClick={toggleCollapsed}
    >
        Réduire
    </SidebarItem>

    <hr>

    <div class="mb-auto">
        {#if fetching}
            <p class="placeholder-glow">
            <button class="btn btn-secondary w-100 placeholder" disabled></button>
            </p>

            <hr/>
        {/if}

        {#if servers && servers.data.length > 0}
            <ul class="nav nav-pills flex-column gap-3">
                {#each servers.data as server}
                    <ServerItem server={server}/>
                {/each}
            </ul>

            <hr/>
        {/if}

        <SidebarItem href="/rent/products/" iconName="plus" className="btn-outline-secondary">
            Louer un serveur
        </SidebarItem>
    </div>

    <hr/>

    <ul class="nav flex-column gap-3">
        <SidebarItem href="/" iconName="chevron-left" className="btn-outline-dark">
            Accueil
        </SidebarItem>

        <SidebarItem href="/rent/products/" iconName="box-arrow-left" className="btn-outline-dark" onClick={logout}>
            Se déconnecter
        </SidebarItem>
    </ul>
</div>