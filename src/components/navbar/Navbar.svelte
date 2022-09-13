<script lang="ts">
	import { locale, t } from "svelte-intl-precompile";
	import Icon from "$components/icons/Icon.svelte";
	import { token, cart } from "$store/store";
	import { page } from "$app/stores";
	import Link from "$shared/Link.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import Cart from "$components/cart/Cart.svelte";
	import { goto } from "$app/navigation";
	import { cartModalVisible } from "$store/store.js";

	let selectedPage: string;

	$: selectedPage = $page.url.pathname.replace(`/${$locale}`, "");

	function openCartModal() {
		$cartModalVisible = true;
	}

	async function redirectToCheckout() {
		await goto(`/${$locale}/checkout/`);
		$cartModalVisible = false;
	}
</script>

<header
	class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 my-lg-4 gap-3"
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
				className="nav-link link-dark {selectedPage === '/' ? 'border-bottom border-dark' : ''}"
			>
				{$t("home")}
			</Link>
		</li>
		<li>
			<Link
				href="/products"
				className="nav-link link-dark {selectedPage === '/products/' ? 'border-bottom border-dark' : ''}"
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

		<div on:click={openCartModal} class="d-inline cursor-pointer text-black p-3">
			<Icon key={$cart.length > 0 ? "bag-check" : "bag"} />
		</div>
	</div>
</header>

<Modal
	bind:visible={$cartModalVisible}
	onClick={redirectToCheckout}
	okText={$cart.length > 0 ? $t("checkout") : null}
	title={$t("cart")}
>
	<Cart />
</Modal>