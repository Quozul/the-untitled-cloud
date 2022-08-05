<script context="module" lang="ts">
	import {
		addMessages,
		getLocaleFromNavigator,
		init,
		waitLocale,
	} from "svelte-intl-precompile";
	import en from "$locales/en.json";
	import fr from "$locales/fr.json";

	/**
	 * @type {import('@sveltejs/kit').Load}
	 */
	export async function load({params}: any) {
		console.log("called", params);
		addMessages("en", en);
		addMessages("fr", fr);

		const initialLocale = getLocaleFromNavigator();

		init({
			fallbackLocale: "en",
			initialLocale,
		});

		await waitLocale(initialLocale);
	}
</script>

<script>
	import Icons from "$components/Icons.svelte";
	import Navbar from "$components/Navbar.svelte";
	import Footer from "$components/Footer.svelte";
</script>

<div class="container min-vh-100 d-flex flex-column justify-content-between">
	<Icons />
	<Navbar />

	<main class="flex-grow-1">
		<slot />
	</main>

	<Footer />
</div>