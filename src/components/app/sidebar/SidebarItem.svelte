<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { sidebarCollapsed } from "$store/store";
	import Link from "../../shared/Link.svelte";

	export let onClick: VoidFunction = null;
	export let href: string = null;
	export let className: string = "btn-outline-primary";
	export let iconName: string = null;
</script>

<style lang="scss">
	.sidebar-item {
		height: 38px;
		overflow: hidden;
		white-space: nowrap;

		:global(.icon) {
			width: 16px;
			height: 16px;
			flex: none;
		}
	}
</style>

{#if href}
	<Link className="sidebar-item btn {className} d-flex align-items-center w-100" {href}>
		{#if iconName}
			<Icon key={iconName} className="me-2 icon"/>
		{/if}

		{#if !$sidebarCollapsed}
			<slot/>
		{/if}
	</Link>
{:else}
	<button class="sidebar-item btn {className} d-flex align-items-center w-100" on:click|preventDefault={onClick}>
		{#if iconName}
			<Icon key={iconName} className="me-2 icon"/>
		{/if}

		{#if !$sidebarCollapsed}
			<slot/>
		{/if}
	</button>
{/if}