<script lang="ts">
	import { onProfilePage, server, sidebarCollapsed } from "$store/store";
	import SidebarItem from "./SidebarItem.svelte";
	import { refreshSelectedServer } from "$components/app/helpers";
	import { goto } from "$app/navigation";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import type { ApiService } from "$models/ApiService";
	import { ApiServiceStatus } from "$enums/ApiServiceStatus";
	import { locale, t } from "svelte-intl-precompile";

	export let service: ApiService;
	let iconName = "box";
	let text: string = service.name ?? service.product.name;
	let className = "btn-outline-dark";
	let classes = "";

	if (service.subscription.status === ApiSubscriptionStatus.PENDING) {
		iconName = "hourglass";
		text = $t("server_status.pending");
		className = "btn-outline-info";
	} else if (service.subscription.status === ApiSubscriptionStatus.CANCELLED) {
		iconName = "archive";
		text = $t("server_status.cancelled");
		className = "btn-outline-info";
	} else if (service.subscription.status === ApiSubscriptionStatus.SUSPENDED) {
		iconName = "pause";
		text = $t("server_status.suspended");
		className = "btn-outline-info";
	} else if (service.state.status === ApiServiceStatus.UNAVAILABLE) {
		iconName = "warning";
		text = $t("server_status.not_found");
		className = "btn-outline-danger";
	}

	$: classes = className + (!$onProfilePage && $server?.id === service?.id ? " active" : "");

	async function setServer() {
		$server = service;
		if ($onProfilePage) {
			$onProfilePage = false;
			await goto(`/${$locale}/dashboard/`);
		}
		await refreshSelectedServer();
		$sidebarCollapsed = true;
	}
</script>

<SidebarItem {iconName} className={classes} onClick={setServer}>
	{text}
</SidebarItem>
