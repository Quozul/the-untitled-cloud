<script lang="ts">
	import { Variant } from "$shared/constants";
	import Icon from "$components/icons/Icon.svelte";
	import { classNames } from "$shared/helpers";
	import { locale } from "svelte-intl-precompile";
	import { page } from "$app/stores";

	export let active = false;
	export let className = "";
	export let disabled = false;
	export let href: string;
	export let icon: string = null;
	export let iconSize = "16";
	export let outline = false;
	export let pill = false;
	export let type = "button";
	export let variant: Variant = Variant.DARK;

	let classes: string;

	$: {
		let validVariant = variant && variant !== Variant.NONE && variant !== Variant.LINK;

		classes = classNames({
			btn: true,
			[`btn${outline ? "-outline" : ""}-${variant}`]: validVariant,
			"rounded-pill": pill,
			disabled: disabled,
			"d-inline-flex": !!icon,
			"align-items-center": !!icon,
			"gap-2": true,
			[className]: !!className,
			"btn-link": variant === Variant.LINK,
			active: active,
		});
	}
</script>

<a {type} href="/{$locale}/dashboard/{$page.params.id}{href}" class={classes} {disabled}>
	{#if icon}
		<Icon key={icon} width={iconSize} height={iconSize} />
	{/if}

	<slot />
</a>
