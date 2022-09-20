import { addMessages, init, waitLocale } from "svelte-intl-precompile";
import fr from "$locales/fr";
import en from "$locales/en";
import { defaultLocale } from "$shared/constants";

/** @type {import("./$types").LayoutLoad} */
export async function load({ params }: any) {
	addMessages("fr", fr);
	addMessages("en", en);

	init({
		fallbackLocale: defaultLocale,
		initialLocale: params.lang,
	});

	await waitLocale(params.lang);
}

// export const csr = true;
// export const ssr = true;
export const prerender = true;
