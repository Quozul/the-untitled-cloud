<script lang="ts">
	import { selectedTab, server } from "$store/store";
	import { ServerSubscriptionStatus, ServerTab } from "$components/app/constants";
	import { goto } from "$app/navigation";

	let isPending: boolean;
	let isEnded: boolean;
	let isSuspended: boolean;

	$: {
		isPending = $server?.subscriptionStatus === ServerSubscriptionStatus.PENDING;
		isEnded = $server?.subscriptionStatus === ServerSubscriptionStatus.ENDED;
		isSuspended = $server?.subscriptionStatus === ServerSubscriptionStatus.SUSPENDED;

		// Set default tabs
		if (isEnded && $selectedTab !== ServerTab.SUBSCRIPTION) {
			goto("/app/subscription/");
		} else if (isPending && $selectedTab === ServerTab.CONSOLE) {
			goto("/app/information/");
		}
	}
</script>

<nav class="nav nav-pills nav-fill flex-column flex-lg-row">
	<a
			class="nav-link"
			class:active={$selectedTab === ServerTab.INFO}
			href="/app/information/"
			disabled="{isEnded}"
	>
		Informations
	</a>

	<a
			class="nav-link"
			class:active={$selectedTab === ServerTab.CONSOLE}
			href="/app/console/"
			disabled="{isPending || isEnded || isSuspended}"
	>
		Console
	</a>

	<a
			class="nav-link"
			class:active={$selectedTab === ServerTab.SUBSCRIPTION}
			href="/app/subscription/"
	>
		Abonnement
	</a>
</nav>