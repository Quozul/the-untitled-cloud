<script lang="ts">
	import { sidebarCollapsed } from "$store/store";
	import SidebarItem from "./SidebarItem.svelte";
	import type { ApiServiceInfo } from "$models/ApiService";
	import { t } from "svelte-intl-precompile";
	import { page } from "$app/stores";

	// Props
	export let service: ApiServiceInfo;

	// State
	let iconName = "box";
	let text: string = service.name ?? service.productName;
	let className = "btn-outline-dark";
	let classes = "";

	if (service.pending) {
		iconName = "hourglass";
		text = $t("server_status.pending");
		className = "btn-outline-info";
	} else if (service.cancelled) {
		iconName = "archive";
		text = $t("server_status.cancelled");
		className = "btn-outline-info";
	} else if (service.suspended) {
		iconName = "pause";
		text = $t("server_status.suspended");
		className = "btn-outline-info";
	} else if (!service.available) {
		iconName = "warning";
		text = $t("server_status.not_found");
		className = "btn-outline-danger";
	}

	$: classes = className + ($page?.params?.id === service.id ? " active" : "");

	function collapseSidebar() {
		$sidebarCollapsed = true;
	}
</script>

<SidebarItem
	{iconName}
	className={classes}
	href="/dashboard/{service.id}/"
	onClick={collapseSidebar}
>
	{text}
</SidebarItem>
