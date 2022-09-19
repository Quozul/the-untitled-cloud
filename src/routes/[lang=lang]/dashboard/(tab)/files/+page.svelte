<script lang="ts">
	import { selectedTab } from "$store/store";
	import { ServerTab } from "$components/app/constants";
	import ServerFtp from "$components/app/tabs/info/ServerFtp.svelte";

	$selectedTab = ServerTab.FILES;
</script>

<ServerFtp />
