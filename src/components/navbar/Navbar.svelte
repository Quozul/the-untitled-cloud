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
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants.js";

	let selectedPage: string;
	let navbarVisible = false;

	$: selectedPage = $page.url.pathname.replace(`/${$locale}`, "");

	function openCartModal() {
		$cartModalVisible = true;
	}

	function toggleNavbar() {
		navbarVisible = !navbarVisible;
	}

	async function redirectToCheckout() {
		await goto(`/${$locale}/checkout/`);
		$cartModalVisible = false;
	}
</script>

<header class="my-lg-4">
	<nav class="navbar navbar-expand-lg">
		<div class="container-fluid d-flex align-items-center gap-3">
			<button on:click={toggleNavbar} class="navbar-toggler">
				<Icon key="list" width="32" height="32" />
			</button>

			<Link
				href="/"
				className="d-flex align-items-center text-dark text-decoration-none flex-lg-grow-1"
			>
				<Icon key="favicon" width="40" height="40" />
				<h4 class="ms-2 mb-0 fw-bold d-none d-lg-block">The Untitled Cloud</h4>
			</Link>

			<Button
				onClick={openCartModal}
				className="text-black order-lg-4"
				variant={Variant.NONE}
			>
				<Icon key={$cart.length > 0 ? "bag-check" : "bag"} />
			</Button>

			<div class="collapse navbar-collapse flex-grow-0 gap-lg-3" class:show={navbarVisible}>
				<ul class="navbar-nav mb-2 mb-lg-0 gap-lg-3">
					<li class="nav-item">
						<Link href="/" className="nav-link {selectedPage === '/' ? 'active' : ''}">
							{$t("home")}
						</Link>
					</li>
					<li class="nav-item">
						<Link
							href="/products/"
							className="nav-link {selectedPage === '/products/' ? 'active' : ''}"
						>
							{$t("products")}
						</Link>
					</li>
				</ul>

				<div class="order-lg-3">
					{#if $token}
						<Link href="/app/" className="btn btn-dark rounded-pill">
							{$t("my_servers")}
						</Link>
					{:else}
						<Link
							href="/login/?redirect=/{$locale}/app"
							className="btn btn-dark rounded-pill"
						>
							{$t("to_login")}
						</Link>
					{/if}
				</div>
			</div>
		</div>
	</nav>
</header>

{#if $cartModalVisible}
	<Modal
		bind:visible={$cartModalVisible}
		onClick={redirectToCheckout}
		okText={$cart.length > 0 ? $t("checkout") : null}
		title={$t("cart")}
		closeText={$t("continue_shopping")}
	>
		<Cart />
	</Modal>
{/if}
