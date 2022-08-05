<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import Icon from "./icons/Icon.svelte";
	import { lang, token } from "$store/store";
	import { page } from "$app/stores";
	import { href } from "$shared/helpers";

	let selectedPage: string;
	$: selectedPage = $page.url.pathname.replace(`/${$lang}`, "");
</script>

<header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
	<a href={href("")} class="d-flex align-items-center text-dark text-decoration-none">
		<Icon key="box" width="40" height="40"/>
	</a>

	<ul class="nav col-12 col-lg-auto mb-2 justify-content-center mb-lg-0">
		<li>
			<a href={href("")} class="nav-link px-2 {selectedPage === '/' ? 'link-secondary' : 'link-dark'}">
				{$t("home")}
			</a>
		</li>
		<li>
			<a href={href("rent/products")} class="nav-link px-2 {selectedPage.startsWith('/rent/') ? 'link-secondary' : 'link-dark'}">
				{$t("products")}
			</a>
		</li>
	</ul>

	<div class="text-end">
		{#if $token}
			<a href={href("app")} class="btn btn-outline-primary me-2">
				{$t("my_servers")}
			</a>
		{:else}
			<a href={href("login/?redirect=/app")} class="btn btn-outline-primary">
				{$t("to_login")}
			</a>
		{/if}
	</div>
</header>