import adapter from "@sveltejs/adapter-static";
import preprocess from "svelte-preprocess";

/** @type {import("@sveltejs/kit").Config} */
const config = {
	// Consult https://github.com/sveltejs/svelte-preprocess
	// for more information about preprocessors
	preprocess: [
		preprocess({
			scss: {
				prependData: '@use "src/variables.scss" as *;',
			},
		}),
	],
	kit: {
		adapter: adapter({
			// default options are shown. On some platforms
			// these options are set automatically — see below
			pages: "build",
			assets: "build",
			fallback: "fallback.html",
			precompress: true,
		}),
		trailingSlash: "always",
		prerender: {
			concurrency: 10,
			enabled: true,
			// This can be false if you're using a fallback (i.e. SPA mode)
			crawl: true,
			entries: ["/fr/", "/en/"],
		},
		alias: {
			$components: "src/components",
			$shared: "src/components/shared",
			$store: "src/store",
			$locales: "src/locales",
			$root: "src",
			$enums: "src/components/api/enums",
			$models: "src/components/api/models",
		},
	},
};

export default config;
