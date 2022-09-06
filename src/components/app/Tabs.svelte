<script lang="ts">
	import { fetchingServer, selectedTab, server } from "$store/store";
	import { ServerTab } from "$components/app/constants";
	import { goto } from "$app/navigation";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { locale } from "svelte-intl-precompile";

	let isPending: boolean;
	let isEnded: boolean;
	let isSuspended: boolean;

	$: {
		isPending = $server?.subscription.status === ApiSubscriptionStatus.PENDING;
		isEnded = $server?.subscription.status === ApiSubscriptionStatus.CANCELLED;
		isSuspended = $server?.subscription.status === ApiSubscriptionStatus.SUSPENDED;

		// Set default tabs
		if (isEnded && $selectedTab !== ServerTab.SUBSCRIPTION) {
			goto(`/${$locale}/app/subscription/`);
		} else if (isPending && $selectedTab === ServerTab.CONSOLE) {
			goto(`/${$locale}/app/`);
		}
	}
</script>

<nav class="nav nav-pills nav-fill flex-column flex-lg-row">
	<a
			class="nav-link"
			class:active={!$fetchingServer && $selectedTab === ServerTab.INFO}
			class:disabled={$fetchingServer || isEnded}
			href="/{$locale}/app/"
	>
		Informations
	</a>

	<a
			class="nav-link"
			class:active={!$fetchingServer && $selectedTab === ServerTab.PARAMETERS}
			class:disabled={$fetchingServer || isPending || isEnded || isSuspended}
			href="/{$locale}/app/parameters/"
	>
		Paramètres
	</a>

	<a
			class="nav-link"
			class:active={!$fetchingServer && $selectedTab === ServerTab.CONSOLE}
			class:disabled={$fetchingServer || isPending || isEnded || isSuspended}
			href="/{$locale}/app/console/"
	>
		Console
	</a>

	<a
			class="nav-link"
			class:active={!$fetchingServer && $selectedTab === ServerTab.SUBSCRIPTION}
			class:disabled={$fetchingServer}
			href="/{$locale}/app/subscription/"
	>
		Abonnement
	</a>
</nav>