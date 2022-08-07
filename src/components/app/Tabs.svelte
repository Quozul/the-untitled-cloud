<script lang="ts">
	import { selectedTab, server } from "$store/store";
	import { ServerSubscriptionStatus, ServerTab } from "$components/app/constants";

	let isPending: boolean;
	let isEnded: boolean;
	let isSuspended: boolean;

	$: {
		isPending = $server?.subscriptionStatus === ServerSubscriptionStatus.PENDING;
		isEnded = $server?.subscriptionStatus === ServerSubscriptionStatus.ENDED;
		isSuspended = $server?.subscriptionStatus === ServerSubscriptionStatus.SUSPENDED;

		// Set default tabs
		if (isEnded && $selectedTab !== ServerTab.SUBSCRIPTION) {
			$selectedTab = ServerTab.SUBSCRIPTION;
		} else if (isPending && $selectedTab === ServerTab.CONSOLE) {
			$selectedTab = ServerTab.INFO;
		}
	}

	async function changeTab(newTab: ServerTab) {
		$selectedTab = newTab;
	}
</script>

<nav class="nav nav-pills nav-fill flex-column flex-lg-row">
	<button
			class="nav-link"
			class:active={$selectedTab === ServerTab.INFO}
			on:click|preventDefault={() => changeTab(ServerTab.INFO)}
			disabled="{isEnded}"
	>
		Informations
	</button>

	<button
			class="nav-link"
			class:active={$selectedTab === ServerTab.CONSOLE}
			on:click|preventDefault={() => changeTab(ServerTab.CONSOLE)}
			disabled="{isPending || isEnded || isSuspended}"
	>
		Console
	</button>

	<button
			class="nav-link"
			class:active={$selectedTab === ServerTab.SUBSCRIPTION}
			on:click|preventDefault={() => changeTab(ServerTab.SUBSCRIPTION)}
	>
		Abonnement
	</button>
</nav>