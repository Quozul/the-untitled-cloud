<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { sidebarCollapsed } from "$store/store";
	import Link from "$shared/Link.svelte";
	import Button from "$shared/Button.svelte";
	import { ButtonVariant } from "$shared/constants.js";

	export let onClick: VoidFunction = null;
	export let href: string = null;
	export let className: string = "btn-outline-primary";
	export let iconName: string = null;
</script>

<style lang="scss" global>
	.sidebar-item {
		height: 38px;
		overflow: hidden;
		white-space: nowrap;

		.icon {
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
	<Button icon={iconName} className="sidebar-item {className} w-100" {onClick} variant={ButtonVariant.NONE}>
		{#if !$sidebarCollapsed}
			<slot/>
		{/if}
	</Button>
{/if}