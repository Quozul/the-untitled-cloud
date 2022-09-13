<script>
	import Icons from "$components/icons/Icons.svelte";
	import Navbar from "$components/navbar/Navbar.svelte";
	import Footer from "$components/Footer.svelte";
	import "$root/app.scss";
</script>

<div class="container min-vh-100 d-flex flex-column justify-content-between">
	<Icons />
	<Navbar />

	<main class="flex-grow-1 d-flex flex-column">
		<slot />
	</main>

	<Footer />
</div>
