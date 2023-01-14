<script lang="ts">
	import { Variant } from "./constants";
	import Icon from "$components/icons/Icon.svelte";
	import { classNames } from "./helpers";

	export let variant: Variant = Variant.DARK;
	export let outline = false;
	export let className = "";
	export let disabled = false;
	export let type = "button";
	export let onClick: VoidFunction = null;
	export let loading = false;
	export let icon: string = null;
	export let iconSize = "16";
	export let pill = false;

	let processing = false;

	let classes: string;

	$: {
		let validVariant = variant && variant !== Variant.NONE && variant !== Variant.LINK;

		classes = classNames({
			btn: true,
			[`btn${outline ? "-outline" : ""}-${variant}`]: validVariant,
			placeholder: loading,
			"rounded-pill": pill,
			[`text-${variant}`]: validVariant && loading,
			disabled: loading || disabled || processing,
			"d-inline-flex": !!icon,
			"align-items-center": !!icon,
			"gap-2": true,
			[className]: !!className,
			"btn-link": variant === Variant.LINK,
		});
	}

	async function handleClick() {
		try {
			processing = true;
			await onClick();
		} finally {
			processing = false;
		}
	}
</script>

<button
	{type}
	class={classes}
	disabled={disabled || processing || loading}
	on:click|preventDefault={handleClick}
>
	{#if processing}
		<span class="icon spinner-border spinner-border-sm" />
	{:else if icon}
		<Icon key={icon} width={iconSize} height={iconSize} />
	{/if}

	<slot />
</button>
