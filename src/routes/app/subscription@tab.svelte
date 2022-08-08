<script lang="ts">
	import SubscriptionInfo from "$components/app/tabs/sub/SubscriptionInfo.svelte";
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";

	$selectedTab = ServerTab.SUBSCRIPTION;
</script>

<SubscriptionInfo/>