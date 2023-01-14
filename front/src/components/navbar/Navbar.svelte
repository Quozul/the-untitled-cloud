<script lang="ts">
	import { locale, t } from "svelte-intl-precompile";
	import Icon from "$components/icons/Icon.svelte";
	import { token, cart, theme } from "$store/store";
	import { page } from "$app/stores";
	import Link from "$shared/Link.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import Cart from "$components/cart/Cart.svelte";
	import { goto } from "$app/navigation";
	import { cartModalVisible } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";

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

	function toggleTheme() {
		$theme = $theme === "light" ? "dark" : "light";
		document.body.setAttribute("data-bs-theme", $theme);
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
				className="order-lg-4 text-dark"
				variant={Variant.NONE}
				icon={$cart?.length === 0 ? "bag" : "bag-check"}
			/>

			<div class="collapse navbar-collapse flex-grow-0 gap-lg-3" class:show={navbarVisible}>
				<ul class="navbar-nav mb-2 mb-lg-0 gap-lg-3">
					<li class="nav-item">
						<Link href="/" className="nav-link {selectedPage === '/' ? 'active' : ''}">
							{$t("common.home")}
						</Link>
					</li>
					<li class="nav-item">
						<Link
							href="/products/"
							className="nav-link {selectedPage === '/products/' ? 'active' : ''}"
						>
							{$t("common.products")}
						</Link>
					</li>
				</ul>

				<div class="order-lg-3 d-inline-block">
					{#if $token}
						<Link href="/dashboard/" className="btn btn-dark rounded-pill">
							{$t("common.my_servers")}
						</Link>
					{:else}
						<Link
							href="/login/?redirect=/{$locale}/dashboard/"
							className="btn btn-dark rounded-pill"
						>
							{$t("authentication.to_login")}
						</Link>
					{/if}
				</div>

				<div class="order-lg-4 d-inline-block">
					<Button
						onClick={toggleTheme}
						className="text-dark"
						variant={Variant.NONE}
						icon={theme === "light" ? "sun" : "moon"}
					/>
				</div>
			</div>
		</div>
	</nav>
</header>

<Modal
	bind:visible={$cartModalVisible}
	onClick={redirectToCheckout}
	okText={$cart?.length === 0 ? null : $t("common.checkout")}
	title={$t("common.cart")}
	closeText={$t("cart.continue_shopping")}
	icon="bag"
>
	<Cart />
</Modal>
