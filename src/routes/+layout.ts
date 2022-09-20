import { addMessages, init, waitLocale } from "svelte-intl-precompile";
import fr from "$locales/fr";
import { defaultLocale } from "$shared/constants";

/** @type {import("./$types").LayoutLoad} */
export async function load({ params }: any) {
	addMessages("fr", fr);

	init({
		fallbackLocale: defaultLocale,
		initialLocale: params.lang,
	});

	await waitLocale(params.lang);
}

// export const csr = true;
// export const ssr = true;
export const prerender = true;
