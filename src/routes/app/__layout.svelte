<script context="module" lang="ts">
	import {
		addMessages,
		init,
		waitLocale,
	} from "svelte-intl-precompile";
	import en from "$locales/en.json";
	import fr from "$locales/fr.json";
	import { defaultLocale } from "$shared/constants";

	/**
	 * @type {import("@sveltejs/kit").Load}
	 */
	export async function load({ params }: any) {
		addMessages("en", en);
		addMessages("fr", fr);

		init({
			fallbackLocale: defaultLocale,
			initialLocale: defaultLocale,
		});

		await waitLocale(defaultLocale);
	}
</script>

<script lang="ts">
	import Sidebar from "$components/app/sidebar/Sidebar.svelte";
	import { token } from "$store/store";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import { refreshSelectedServer } from "$components/app/helpers";
	import Icons from "$components/icons/Icons.svelte";
	import "$root/app.scss";

	onMount(async () => {
		if (!$token) {
			await goto("/login?redirect=/app");
		}

		await refreshSelectedServer();
	});
</script>

<svelte:head>
	<title>Mes serveurs</title>
</svelte:head>

<style lang="scss">
	@include media-breakpoint-down(sm) {
		.content {
			margin-left: 74px;
		}
	}
</style>

<Icons/>

<div class="d-flex vh-100">
	<Sidebar/>

	<div class="content overflow-auto flex-grow-1 p-3 d-flex flex-column gap-3">
		<slot/>
	</div>
</div>