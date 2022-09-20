<script>
	import { t } from "svelte-intl-precompile";
	import { page } from "$app/stores";
	import Navbar from "$components/navbar/Navbar.svelte";
	import Icon from "$components/icons/Icon.svelte";
	import Footer from "$components/Footer.svelte";
	import Icons from "$components/icons/Icons.svelte";
	import Link from "$shared/Link.svelte";
	import "$root/app.scss";
</script>

<div class="container min-vh-100 d-flex flex-column">
	<Icons />
	<header>
		<Navbar />
	</header>

	<section
		class="home flex-grow-1 d-flex flex-column align-items-center justify-content-center text-center"
	>
		<div>
			<div class="icon-container">
				<Icon key="favicon" width="192" height="192" />
			</div>

			<h1 class="display-1">{$page.status}</h1>
			<p class="lead text-dark fs-2">
				{$page.error.message}
			</p>

			<Link href="/" className="btn btn-dark btn-lg">{$t("common.home")}</Link>
		</div>
	</section>

	<Footer />
</div>
