<script lang="ts">
	import { refreshAllServers, refreshSelectedServer } from "$components/app/helpers";
	import Button from "$shared/Button.svelte";

	async function refresh() {
		await Promise.all([refreshAllServers(), refreshSelectedServer()]);
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>En attente</h4>

	<p class="lead">
		Votre serveur est en cours de préparation.<br />
		Rafraichissez la liste pour voir son nouvel état.
	</p>

	<Button onClick={refresh}>Rafraichir</Button>
</div>
