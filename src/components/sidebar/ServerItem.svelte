<script lang="ts">
	import { onProfilePage, sidebarCollapsed } from "$store/store";
	import SidebarItem from "./SidebarItem.svelte";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import type { ApiService } from "$models/ApiService";
	import { ApiServiceStatus } from "$enums/ApiServiceStatus";
	import { t } from "svelte-intl-precompile";
	import { page } from "$app/stores";

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

	$: classes = className + (!$onProfilePage && $page?.params?.id === service.id ? " active" : "");

	function collapseSidebar() {
		$sidebarCollapsed = true;
	}
</script>

<SidebarItem {iconName} className={classes} href="/dashboard/{service.id}/" onClick={collapseSidebar}>
	{text}
</SidebarItem>
