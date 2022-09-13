<script lang="ts">
	import { locale, t } from "svelte-intl-precompile";
	import Icon from "$components/icons/Icon.svelte";
	import { token } from "$store/store";
	import { page } from "$app/stores";
	import Link from "$shared/Link.svelte";

	let selectedPage: string;
	$: selectedPage = $page.url.pathname.replace(`/${$locale}`, "");
</script>

<header
	class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 my-lg-4"
>
	<Link href="/" className="d-flex align-items-center text-dark text-decoration-none flex-grow-1">
		<Icon key="favicon" width="40" height="40" />
		<h4 class="ms-2 mb-0 fw-bold">
			The Untitled Cloud
		</h4>
	</Link>

	<ul class="nav col-12 col-lg-auto mb-2 justify-content-center mb-lg-0">
		<li>
			<Link
				href="/"
				className="nav-link {selectedPage === '/' ? 'link-secondary' : 'link-dark'}"
			>
				{$t("home")}
			</Link>
		</li>
		<li>
			<Link
				href="/rent/products"
				className="nav-link {selectedPage.startsWith('/rent/')
					? 'link-secondary'
					: 'link-dark'}"
			>
				{$t("products")}
			</Link>
		</li>
	</ul>

	<div class="text-end">
		{#if $token}
			<Link href="/app/" className="btn btn-dark rounded-pill">
				{$t("my_servers")}
			</Link>
		{:else}
			<Link href="/login/?redirect=/{$locale}/app" className="btn btn-dark rounded-pill">
				{$t("to_login")}
			</Link>
		{/if}
	</div>
</header>
