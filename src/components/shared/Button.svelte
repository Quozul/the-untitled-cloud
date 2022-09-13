<script lang="ts">
	import { Variant } from "./constants";
	import Icon from "$components/icons/Icon.svelte";
	import { classNames } from "./helpers";

	export let variant: Variant = Variant.PRIMARY;
	export let outline: boolean = false;
	export let className: string = "";
	export let disabled: boolean = false;
	export let type: string = "button";
	export let onClick: VoidFunction = null;
	export let loading: boolean = false;
	export let icon: string = null;
	export let iconSize: string = "16";

	let processing = false;

	let classes: string;

	$: classes = classNames({
		btn: true,
		[`btn${outline ? "-outline" : ""}-${variant}`]: true,
		placeholder: loading,
		[`text-${variant}`]: loading,
		disabled: loading || disabled || processing,
		"d-inline-flex": !!icon,
		"align-items-center": !!icon,
		"gap-2": true,
		[className]: !!className,
	});

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
