<script>
	import { locale } from "svelte-intl-precompile";
	import { page } from "$app/stores";

	$locale = $page.params.lang;
</script>

<slot/>
