<script lang="ts">
	import { locale } from "svelte-intl-precompile";

	const site = "The Untitled Cloud";
	const author = "The Untitled Cloud";
	const canonical = "https://beta.theuntitledcloud.com/"
	const email = "contact@theuntitledcloud.com"

	export let title: string;
	export let description: string;
</script>

<svelte:head>
	<title>{title} | {site}</title>

	<meta property="og:title" content={title}>
	<meta name="author" content={author}>
	<meta property="og:locale" content="{$locale}">
	<meta name="description" content={description}>
	<meta property="og:description" content={description}>
	<link rel="canonical" href={canonical}>
	<meta property="og:url" content={canonical}>
	<meta property="og:site_name" content={site}>
	<meta name="twitter:card" content="summary">
	<meta property="twitter:title" content={title}>

	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="color-scheme" content="light">
	<meta name="coverage" content="Worldwide">
	<meta name="distribution" content="Local">
	<meta name="rating" content="General">
	<meta name="og:email" property="og:email" content={email}>

	<slot/>
</svelte:head>