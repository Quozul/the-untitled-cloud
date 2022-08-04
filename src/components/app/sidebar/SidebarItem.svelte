<script lang="ts">
    import Icon from "../../icons/Icon.svelte";
	import { sidebarCollapsed } from "../../../store/store";

	export let onClick: VoidFunction = null;
	export let href: string = null;
	export let className: string = "btn-outline-primary";
	export let iconName: string = "box";
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
    <a class="sidebar-item btn {className} d-flex align-items-center w-100" {href}>
        <Icon key={iconName} className="me-2 icon"/>
        {#if !$sidebarCollapsed}
            <slot/>
        {/if}
    </a>
{:else}
    <button class="sidebar-item btn {className} d-flex align-items-center w-100" on:click|preventDefault={onClick}>
        <Icon key={iconName} className="me-2 icon"/>
        {#if !$sidebarCollapsed}
            <slot/>
        {/if}
    </button>
{/if}