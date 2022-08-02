<script>
	import { token } from "../store/store";
	import Product from "../components/cart/Product.svelte";
</script>

<div class="px-4 py-5">
    <div class="row flex-lg-row align-items-center g-5 py-5">
        <div class="col-lg-6">
            <h1 class="display-5 fw-bold lh-1 mb-3">Le meilleur hébergeur Minecraft.</h1>
            <p class="lead">
                Cum haec taliaque sollicitas eius aures everberarent expositas semper eius modi rumoribus et
                patentes, varia animo tum miscente consilia, tandem id ut optimum factu elegit.
            </p>

            <div class="d-grid gap-2 d-md-flex justify-content-md-start">
                <a href="/rent/products/" class="btn btn-primary btn-lg px-4 me-md-2">Louer un serveur</a>

                {#if $token}
                    <a href="/app/" class="btn btn-outline-secondary btn-lg px-4">
                        Mes serveurs
                    </a>
                {:else}
                    <a href="/login/?redirect=/app" class="btn btn-outline-secondary btn-lg px-4">
                        Se connecter
                    </a>
                {/if}
            </div>
        </div>

        <div class="col-md-10 mx-auto col-lg-5">
            <div class="p-4 p-md-5">
                <Product/>
            </div>
        </div>
    </div>
</div>