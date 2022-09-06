<script lang="ts">
	import InfoTab from "$components/app/InfoTab.svelte";
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";

	$selectedTab = ServerTab.INFO;
</script>

<InfoTab/>