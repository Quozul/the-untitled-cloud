<script lang="ts">
	import { locale } from "svelte-intl-precompile";
	import { defaultLocale } from "./constants";

	export let href: string = "/";
	export let className: string = "";

	const isExternalLink = href.startsWith("http");

	let internalLink: string = null;

	$: if (!isExternalLink) {
		if ($locale === defaultLocale || !$locale) {
			internalLink = href;
		} else {
			internalLink = `/${$locale}${href}`;
		}
	}
</script>

{#if isExternalLink}
	<a {href} class={className} target="_blank" rel="noreferrer noopener">
		<slot/>
	</a>
{:else}
	<a href={internalLink} class={className}>
		<slot/>
	</a>
{/if}