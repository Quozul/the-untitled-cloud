<script>
	import Navbar from "../components/Navbar.svelte";
	import "../app.scss";
	import Icons from "../components/Icons.svelte";
	import Footer from "../components/Footer.svelte";
</script>

<div class="container min-vh-100 d-flex flex-column justify-content-between">
    <Icons />
    <Navbar />

    <main class="flex-grow-1">
        <slot />
    </main>

    <Footer />
</div>