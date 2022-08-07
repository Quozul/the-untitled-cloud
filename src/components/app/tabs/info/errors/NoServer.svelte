<script lang="ts">
    import Link from "$shared/Link.svelte";
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
    <h4>C'est vide</h4>
    <p class="lead">
        Il semblerait que vous n'ayez pas encore de serveur.<br/>
        Commencez par louer un serveur ou en sélectionner un sur la gauche de l'écran si vous en avez déjà un.
    </p>
    <Link href="/rent/products/" className="btn btn-primary">Louer un serveur</Link>
</div>