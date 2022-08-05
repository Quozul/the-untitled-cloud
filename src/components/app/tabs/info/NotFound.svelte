<script lang="ts">
	import { patchServer, toggleRefreshServerInfo, toggleRefreshServerList } from "$components/app/helpers";
	import { selectedServer } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { ButtonVariant } from "$shared/constants";

	async function recreate() {
		await patchServer($selectedServer.id, "RECREATE");
		toggleRefreshServerList();
		toggleRefreshServerInfo();
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>Introuvable</h4>
	<p class="lead">
		Nous n'avons pas réussi à trouver votre serveur.<br/>
		Essayez de le recréer.
	</p>

	<Button variant={ButtonVariant.DANGER} onClick={recreate}>
		Recréer
	</Button>
</div>