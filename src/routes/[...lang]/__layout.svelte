<script context="module" lang="ts">
	import {
		addMessages,
		getLocaleFromNavigator,
		init,
		waitLocale,
	} from "svelte-intl-precompile";
	import en from "$locales/en.json";
	import fr from "$locales/fr.json";
	import { lang } from "$store/store";

	/**
	 * @type {import('@sveltejs/kit').Load}
	 */
	export async function load({params}: any) {
		addMessages("en", en);
		addMessages("fr", fr);

		const paramLang = params.lang.match(/[a-z-A-Z]+/)?.[0];
		lang.update(() => paramLang);
		const initialLocale = paramLang || getLocaleFromNavigator();

		init({
			fallbackLocale: "en",
			initialLocale,
		});

		await waitLocale(initialLocale);
	}
</script>

<script>
	import Icons from "$components/icons/Icons.svelte";
	import Navbar from "$components/Navbar.svelte";
	import Footer from "$components/Footer.svelte";
	import "$root/app.scss";
</script>

<div class="container min-vh-100 d-flex flex-column justify-content-between">
	<Icons />
	<Navbar />

	<main class="flex-grow-1">
		<slot />
	</main>

	<Footer />
</div>