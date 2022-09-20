<script lang="ts">
	import { page } from "$app/stores";
	import { getStripePortal } from "$components/app/helpers";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";

	async function redirectToStripe() {
		const redirect = $page.url.href;
		const { response } = await getStripePortal(redirect);
		window.open(response.url);
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>Suspendu</h4>

	<p class="lead">
		Votre serveur est suspendu.<br />
		Ceci peut être dû à un problème de paiement ou bien parce que vous avez annulé l'abonnement.
		<br />
		Consultez l'onglet Abonnement pour plus d'informations.
	</p>

	<Button icon="box-arrow-up-right" onClick={redirectToStripe} variant={Variant.SECONDARY}>
		Voir mon abonnement
	</Button>
</div>
