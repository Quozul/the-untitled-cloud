<script lang="ts">
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";
	import Parameters from "$components/parameters/Parameters.svelte";

	$selectedTab = ServerTab.PARAMETERS;
</script>

<Parameters />
