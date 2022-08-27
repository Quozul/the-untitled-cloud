<script lang="ts">
	import { locale, t } from "svelte-intl-precompile";
	import Icon from "./icons/Icon.svelte";
	import { token } from "$store/store";
	import { page } from "$app/stores";
	import Link from "./shared/Link.svelte";

	let selectedPage: string;
	$: selectedPage = $page.url.pathname.replace(`/${$locale}`, "");
</script>

<header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
	<Link href="/" className="d-flex align-items-center text-dark text-decoration-none">
		<Icon key="favicon" width="40" height="40"/>
	</Link>

	<ul class="nav col-12 col-lg-auto mb-2 justify-content-center mb-lg-0">
		<li>
			<Link href="/" className="nav-link px-2 {selectedPage === '/' ? 'link-secondary' : 'link-dark'}">
				{$t("home")}
			</Link>
		</li>
		<li>
			<Link href="/rent/products" className="nav-link px-2 {selectedPage.startsWith('/rent/') ? 'link-secondary' : 'link-dark'}">
				{$t("products")}
			</Link>
		</li>
	</ul>

	<div class="text-end">
		{#if $token}
			<a href="/app/" class="btn btn-outline-primary me-2">
				{$t("my_servers")}
			</a>
		{:else}
			<Link href="/login/?redirect=/app" className="btn btn-outline-primary">
				{$t("to_login")}
			</Link>
		{/if}
	</div>
</header>