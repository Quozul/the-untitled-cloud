<script lang="ts">
	import { locale } from "svelte-intl-precompile";

	export let href: string = "/";
	export let className: string = "";
	export let onClick: VoidFunction = null;

	const isExternalLink = href.startsWith("http");

	let internalLink: string = null;

	$: if (!isExternalLink) {
		if (!$locale) {
			internalLink = href;
		} else {
			internalLink = `/${$locale}${href}`;
		}
	}
</script>

{#if isExternalLink}
	<a {href} class={className} target="_blank" rel="noreferrer noopener" on:click={onClick}>
		<slot/>
	</a>
{:else}
	<a href={internalLink} class={className} on:click={onClick}>
		<slot/>
	</a>
{/if}