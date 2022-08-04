<script lang="ts">
	import Icon from "./icons/Icon.svelte";
	import { token } from "../store/store";
    import { page } from "$app/stores";

    let selectedPage: string;
    $: selectedPage = $page.url.pathname;
</script>

<header class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
    <a href="/" class="d-flex align-items-center text-dark text-decoration-none">
        <Icon key="box" width="40" height="40"/>
    </a>

    <ul class="nav col-12 col-lg-auto mb-2 justify-content-center mb-lg-0">
        <li>
            <a href="/" class="nav-link px-2 {selectedPage === '/' ? 'link-secondary' : 'link-dark'}">Accueil</a>
        </li>
        <li>
            <a href="/rent/products/" class="nav-link px-2 {selectedPage.startsWith('/rent/') ? 'link-secondary' : 'link-dark'}">Produits</a>
        </li>
    </ul>

    <div class="text-end">
        {#if $token}
            <a href="/app/" class="btn btn-outline-primary me-2">
                Mes serveurs
            </a>
        {:else}
            <a href="/login/?redirect=/app" class="btn btn-outline-primary me-2">
                Se connecter
            </a>
        {/if}
    </div>
</header>