<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
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

		<span class="content">
			<slot />
		</span>
	</Link>
{:else}
	<Button icon={iconName} className="sidebar-item {className}" {onClick} variant={Variant.NONE}>
		<span class="content">
			<slot />
		</span>
	</Button>
{/if}

<style lang="scss" global>
	.sidebar-item {
		height: 38px;
		white-space: nowrap;

		.content {
			overflow: hidden;
			text-overflow: ellipsis;
		}
	}
</style>
