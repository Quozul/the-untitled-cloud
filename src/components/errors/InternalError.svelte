<script lang="ts">
	import Button from "$shared/Button.svelte";

	export let refresh: VoidFunction;
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>Erreur</h4>
	<p class="lead">
		Nous n'avons pas réussi à contacter notre serveur.<br />
		Vérifiez votre connexion internet puis essayer de rafraichir la page.
	</p>
	<Button onClick={refresh}>Rafraichir</Button>
</div>
