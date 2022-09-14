<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { sidebarCollapsed } from "$store/store";
	import Link from "$shared/Link.svelte";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";

	export let onClick: VoidFunction = null;
	export let href: string = null;
	export let className = "btn-outline-primary";
	export let iconName: string = null;
</script>

{#if href}
	<Link className="sidebar-item btn {className} d-flex align-items-center gap-2" {href}>
		{#if iconName}
			<Icon key={iconName} />
		{/if}

		{#if !$sidebarCollapsed}
			<slot />
		{/if}
	</Link>
{:else}
	<Button icon={iconName} className="sidebar-item {className}" {onClick} variant={Variant.NONE}>
		{#if !$sidebarCollapsed}
			<slot />
		{/if}
	</Button>
{/if}

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
