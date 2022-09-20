import { get } from "svelte/store";
import { sidebarCollapsed } from "$store/store";

export const toggleSidebarCollapsed = () => {
	sidebarCollapsed.set(!get(sidebarCollapsed));
};
