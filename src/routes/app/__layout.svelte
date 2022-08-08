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

<script>
    import Icons from "$components/icons/Icons.svelte";
	import "$root/app.scss";
</script>

<Icons/>
<slot/>