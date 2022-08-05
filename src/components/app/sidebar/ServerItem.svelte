<script lang="ts">
	import type { Server } from "$components/app/models";
	import { selectedServer } from "$store/store";
	import { ServerSubscriptionStatus } from "$components/app/constants";
	import SidebarItem from "./SidebarItem.svelte";

	export let server: Server;
	let iconName: string = "box";
	let text: string = server.name;
	let className: string = "btn-outline-primary";
	let classes: string = "";

    if (server.subscriptionStatus === ServerSubscriptionStatus.PENDING) {
        iconName = "hourglass";
        text = "En attente";
        className = "btn-outline-info";
    } else if (server.subscriptionStatus === ServerSubscriptionStatus.ENDED) {
		iconName = "archive";
		text = "Terminé";
		className = "btn-outline-info";
    } else if (server.subscriptionStatus === ServerSubscriptionStatus.SUSPENDED) {
		iconName = "pause";
		text = "Suspendu";
		className = "btn-outline-info";
    } else if (!server.serverStatus) {
		iconName = "warning";
		text = "Serveur introuvable";
		className = "btn-outline-danger";
    }

	$: classes = className + ($selectedServer?.id === server.id ? " active" : "");

	const setServer = () => {
		$selectedServer = server;
    }
</script>

<SidebarItem {iconName} className={classes} onClick={setServer}>
    {text}
</SidebarItem>