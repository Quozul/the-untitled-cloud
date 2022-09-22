<script>
	import { locale } from "svelte-intl-precompile";
	import { page } from "$app/stores";
	import { defaultLocale } from "$shared/constants";

	$locale = $page.params.lang || defaultLocale;
</script>

<slot />
