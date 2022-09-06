<script lang="ts">
	import Console from "$components/app/tabs/console/Console.svelte";
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";

	$selectedTab = ServerTab.CONSOLE;
</script>

<Console/>