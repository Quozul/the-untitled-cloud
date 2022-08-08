<script lang="ts">
	import Parameters from "$components/app/tabs/info/Parameters.svelte";
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";

	$selectedTab = ServerTab.PARAMETERS;
</script>

<Parameters/>