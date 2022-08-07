<script lang="ts">
    import { onMount } from "svelte";
    import {
        sidebarCollapsed,
        token,
        fetchingServers,
        fetchServersError,
        servers,
    } from "$store/store";
	import type { Paginate, Server } from "$components/app/models";
	import Icon from "$components/icons/Icon.svelte";
	import { goto } from "$app/navigation";
	import SidebarItem from "./SidebarItem.svelte";
	import ServerItem from "./ServerItem.svelte";
    import { getAllServers, refreshAllServers } from "$components/app/helpers";
    import Link from "$shared/Link.svelte";
    import { mergePaginate } from "$shared/helpers";

	// State
	let endedServers: Paginate<Server>;

    onMount(async () => {
        await refreshAllServers();
    });

    async function loadMoreServers() {
        await refreshAllServers($servers.page + 1);
    }

    async function loadEndedServers() {
        if (endedServers && !endedServers?.lastPage) {
            const response = await getAllServers(endedServers.page + 1, true);
            endedServers = mergePaginate(endedServers, response);
        } else {
            endedServers = await getAllServers(0, true);
        }
    }

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
		width: 300px;
        height: 100%;
        overflow-y: auto;

		&.collapsed {
			width: 74px;
		}

        .sidebar-item {
            height: 38px;
        }
	}
</style>

<div class="d-flex flex-column flex-shrink-0 p-3 bg-light sidebar shadow-sm" class:collapsed={$sidebarCollapsed}>
    <Link href="/" className="d-flex align-items-center me-md-auto link-dark text-decoration-none">
        <Icon key="box" width="42" height="38" className={!$sidebarCollapsed && "me-2"}/>
        {#if !$sidebarCollapsed}
            <span class="fs-4">Quozul.cloud</span>
        {/if}
    </Link>

    <div class="d-flex gap-3">
        <SidebarItem
            iconName={$sidebarCollapsed ? "chevron-double-right" : "chevron-double-left"}
            className="btn-outline-secondary mt-3 collapse-button flex-grow-1"
            onClick={toggleCollapsed}
        >
            Réduire
        </SidebarItem>

        {#if !$sidebarCollapsed}
            <SidebarItem
                iconName="arrow-clockwise"
                className="btn-outline-secondary mt-3 collapse-button"
                onClick={refreshAllServers}
            />
        {/if}
    </div>

    <hr>

    <div class="mb-auto">
        {#if $fetchingServers}
            <p class="placeholder-glow">
            <button class="btn btn-secondary w-100 placeholder" disabled></button>
            </p>

            <hr/>
        {/if}

        {#if $fetchServersError}
            <div class="d-flex flex-column gap-3">
                <SidebarItem className="btn-outline-danger" onClick={refreshAllServers}>
                    <Icon key="warning"/>
                    Rafraichir la liste
                </SidebarItem>
            </div>

            <hr/>
        {/if}

        {#if $servers.data.length > 0}
            <div class="d-flex flex-column gap-3">
                {#each $servers.data as server}
                    <ServerItem server={server}/>
                {/each}

                {#if !$servers.lastPage}
                    <SidebarItem className="btn-outline-secondary" iconName="plus" onClick={loadMoreServers}>
                        Charger plus
                    </SidebarItem>
                {/if}
            </div>

            <hr/>
        {/if}

        <div class="d-flex flex-column gap-3">
            <SidebarItem href="/rent/products/" iconName="plus" className="btn-outline-secondary">
                Louer un serveur
            </SidebarItem>

            {#if !endedServers}
                <SidebarItem className="btn-outline-secondary" iconName="more" onClick={loadEndedServers}>
                    Charger les ancien serveur
                </SidebarItem>
            {/if}
        </div>

        {#if endedServers && endedServers.data.length > 0}
            <hr/>

            <div class="d-flex flex-column gap-3">
                <h6 class="px-2 py-1 m-0 fw-bold" class:visually-hidden={$sidebarCollapsed}>
                    <Icon/>
                    Anciens serveurs
                </h6>

                {#each endedServers.data as server}
                    <ServerItem server={server}/>
                {/each}

                {#if !endedServers.lastPage}
                    <SidebarItem className="btn-outline-secondary" iconName="plus" onClick={loadEndedServers}>
                        Charger plus
                    </SidebarItem>
                {/if}
            </div>
        {:else if endedServers && endedServers.data.length === 0}
            <hr/>

            <h6 class="px-2 py-1 m-0 fw-bold" class:visually-hidden={$sidebarCollapsed}>
                <Icon/>
                Aucun anciens serveur
            </h6>
        {/if}
    </div>

    <hr/>

    <ul class="nav flex-column gap-3">
        <SidebarItem href="/" iconName="chevron-left" className="btn-outline-dark">
            Accueil
        </SidebarItem>

        <SidebarItem iconName="box-arrow-left" className="btn-outline-dark" onClick={logout}>
            Se déconnecter
        </SidebarItem>
    </ul>
</div>