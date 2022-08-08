<script lang="ts">
	import NewParameters from "$components/parameters/NewParameters.svelte";
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";

	$selectedTab = ServerTab.PARAMETERS;
</script>

<NewParameters/>