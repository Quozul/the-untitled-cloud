<script lang="ts">
	import { onMount } from "svelte";
	import {
		sidebarCollapsed,
		token,
		fetchingServers,
		fetchServersError,
		servers,
		onProfilePage,
		user,
	} from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import { goto } from "$app/navigation";
	import SidebarItem from "./SidebarItem.svelte";
	import ServerItem from "./ServerItem.svelte";
	import { getAllServers, refreshAllServers } from "$components/app/helpers";
	import { mergePaginate } from "$shared/helpers";
	import type { ApiService } from "$models/ApiService";
	import type { ApiPaginate } from "$models/ApiPaginate";
	import { locale, t } from "svelte-intl-precompile";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";
	import { toggleSidebarCollapsed } from "$components/sidebar/helpers";

	// State
	let endedServers: ApiPaginate<ApiService>;

	onMount(async () => {
		await refreshAllServers();
	});

	async function loadMoreServers() {
		await refreshAllServers($servers.page + 1);
	}

	async function loadEndedServers() {
		if (endedServers && !endedServers?.lastPage) {
			const { response } = await getAllServers(endedServers.page + 1, true);
			endedServers = mergePaginate(endedServers, response);
		} else {
			const { response } = await getAllServers(0, true);
			endedServers = response;
		}
	}

	async function logout() {
		$token = null;
		$user = null;
		await goto(`/${$locale}/`);
	}

	function hide() {
		$sidebarCollapsed = true;
	}
</script>

<div class="backdrop bg-dark d-sm-none" class:d-none={$sidebarCollapsed} on:click={hide} />

<div
	class="d-flex flex-column flex-shrink-0 bg-light sidebar shadow-sm py-3 d-lg-flex"
	class:d-none={$sidebarCollapsed}
>
	<div class="sidebar-header d-flex align-items-center gap-2 px-3">
		<Icon
			key="favicon"
			width="42"
			height="38"
			className={!$sidebarCollapsed && "d-none d-lg-inline"}
		/>
		<Button
			icon="x-lg"
			onClick={toggleSidebarCollapsed}
			variant={Variant.LIGHT}
			className="d-inline d-lg-none"
		/>
		<span class="fw-bolder m-0 fs-5">The Untitled Cloud</span>
	</div>

	<hr />

	<div class="mb-auto px-3">
		{#if $fetchingServers}
			<p class="placeholder-glow">
				<button class="btn btn-secondary w-100 placeholder" disabled />
			</p>

			<hr />
		{/if}

		{#if $fetchServersError}
			<div class="d-flex flex-column gap-3">
				<SidebarItem
					className="btn-outline-danger"
					iconName="warning"
					onClick={refreshAllServers}
				>
					{$t("sidebar.refresh_list")}
				</SidebarItem>
			</div>

			<hr />
		{/if}

		{#if $servers?.data?.length > 0}
			<div class="d-flex flex-column gap-3">
				<h6 class="px-2 py-1 m-0 fw-bold">
					<Icon />
					{$t("common.my_servers")} ({$servers.totalElements})
				</h6>

				{#each $servers.data as service}
					<ServerItem {service} />
				{/each}

				{#if !$servers.lastPage}
					<SidebarItem
						className="btn-outline-secondary"
						iconName="plus"
						onClick={loadMoreServers}
					>
						{$t("common.load_more")}
					</SidebarItem>
				{/if}
			</div>

			<hr />
		{/if}

		<div class="d-flex flex-column gap-3">
			<SidebarItem href="/products/" iconName="plus" className="btn-outline-secondary">
				{$t("common.rent_a_server")}
			</SidebarItem>

			{#if !endedServers}
				<SidebarItem
					className="btn-outline-secondary"
					iconName="more"
					onClick={loadEndedServers}
				>
					{$t("sidebar.load_old_servers")}
				</SidebarItem>
			{/if}
		</div>

		{#if endedServers && endedServers.data.length > 0}
			<hr />

			<div class="d-flex flex-column gap-3">
				<h6 class="px-2 py-1 m-0 fw-bold">
					<Icon />
					{$t("sidebar.old_servers")} ({endedServers.totalElements})
				</h6>

				{#each endedServers.data as server}
					<ServerItem service={server} />
				{/each}

				{#if !endedServers.lastPage}
					<SidebarItem
						className="btn-outline-secondary"
						iconName="plus"
						onClick={loadEndedServers}
					>
						{$t("common.load_more")}
					</SidebarItem>
				{/if}
			</div>
		{:else if endedServers && endedServers.data.length === 0}
			<hr />

			<h6 class="px-2 py-1 m-0 fw-bold">
				<Icon />
				{$t("sidebar.no_old_servers")}
			</h6>
		{/if}
	</div>

	<hr />

	<ul class="nav flex-column gap-3 px-3">
		<SidebarItem
			href="/dashboard/profile/"
			iconName="person"
			className="btn-outline-dark {$onProfilePage ? 'active' : ''}"
		>
			{$t("sidebar.my_profile")}
		</SidebarItem>

		<SidebarItem href="/" iconName="chevron-left" className="btn-outline-dark">
			{$t("common.home")}
		</SidebarItem>

		<SidebarItem iconName="box-arrow-left" className="btn-outline-dark" onClick={logout}>
			{$t("authentication.to_logout")}
		</SidebarItem>
	</ul>
</div>

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

		.sidebar-item {
			height: 38px;
		}
	}
</style>
