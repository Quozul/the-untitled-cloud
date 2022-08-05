<script lang="ts">
	import { toggleRefreshServerInfo, toggleRefreshServerList } from "$components/app/helpers";
	import { ServerTab } from "$components/app/constants";
	import { selectedTab } from "$store/store";

	function refresh() {
		toggleRefreshServerList();
		toggleRefreshServerInfo();
	}

	function goToSubscriptionTab() {
		$selectedTab = ServerTab.SUBSCRIPTION
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>Suspendu</h4>

	<p class="lead">
		Votre serveur est suspendu.<br/>
		Ceci peut être dû à un problème de paiement ou bien parce que vous avez annulé l'abonnement.<br/>
		Consultez l'onglet Abonnement pour plus d'informations.
	</p>

	<button class="btn btn-secondary" on:click={goToSubscriptionTab}>Voir mon abonnement</button>
</div>