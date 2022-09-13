import { addMessages, init, locale, waitLocale } from "svelte-intl-precompile";
import en from "$locales/en.json";
import fr from "$locales/fr.json";
import { defaultLocale } from "$shared/constants";

/** @type {import("./$types").LayoutLoad} */
export async function load({ params }: any) {
	addMessages("en", en);
	addMessages("fr", fr);

	init({
		fallbackLocale: defaultLocale,
		initialLocale: params.lang,
	});

	await waitLocale(params.lang);
}
