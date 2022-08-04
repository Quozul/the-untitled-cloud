<script lang="ts">
	import type { Server } from "../models";
	import { selectedServer } from "../../../store/store.js";
	import { ServerSubscriptionStatus } from "../constants.js";
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