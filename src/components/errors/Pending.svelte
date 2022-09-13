<script lang="ts">
	import { refreshAllServers, refreshSelectedServer } from "../app/helpers";

	function refresh() {
		refreshAllServers();
		refreshSelectedServer();
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>En attente</h4>

	<p class="lead">
		Votre serveur est en cours de préparation.<br />
		Rafraichissez la liste pour voir son nouvel état.
	</p>

	<button class="btn btn-primary" on:click|preventDefault={refresh}>Rafraichir</button>
</div>
