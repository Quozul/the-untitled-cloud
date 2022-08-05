<script>
	import Product from "$components/cart/Product.svelte";
    import Hero from "$components/hero/Hero.svelte";
</script>

<svelte:head>
    <title>Accueil</title>
</svelte:head>

<div class="px-4 py-5">
    <div class="row flex-lg-row align-items-center g-5 py-5">
        <div class="col-lg-6">
            <Hero/>
        </div>

        <div class="col-md-10 mx-auto col-lg-5">
            <div class="p-4 p-md-5">
                <Product/>
            </div>
        </div>
    </div>
</div>