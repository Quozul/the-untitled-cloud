<script lang="ts">
	import {
		patchServer,
		refreshAllServers,
		refreshSelectedServer,
	} from "$components/app/helpers";
	import { server } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";

	async function recreate() {
		await patchServer($server, "RECREATE");
		refreshAllServers();
		refreshSelectedServer();
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>Introuvable</h4>
	<p class="lead">
		Nous n'avons pas réussi à trouver votre serveur.<br/>
		Essayez de le recréer.
	</p>

	<Button variant={Variant.DANGER} onClick={recreate}>
		Recréer
	</Button>
</div>