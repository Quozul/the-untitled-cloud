<script lang="ts">
    import { onProfilePage, server } from "$store/store";
	import SidebarItem from "./SidebarItem.svelte";
    import { refreshSelectedServer } from "$components/app/helpers";
    import { goto } from "$app/navigation";
    import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
    import type { ApiService } from "$models/ApiService";
    import { ApiServiceStatus } from "$enums/ApiServiceStatus";
    import { locale } from "svelte-intl-precompile";

	export let service: ApiService;
	let iconName: string = "box";
	let text: string = service.name ?? service.product.name;
	let className: string = "btn-outline-primary";
	let classes: string = "";

    if (service.subscription.status === ApiSubscriptionStatus.PENDING) {
        iconName = "hourglass";
        text = "En attente";
        className = "btn-outline-info";
    } else if (service.subscription.status === ApiSubscriptionStatus.CANCELLED) {
		iconName = "archive";
		text = "Terminé";
		className = "btn-outline-info";
    } else if (service.subscription.status === ApiSubscriptionStatus.SUSPENDED) {
		iconName = "pause";
		text = "Suspendu";
		className = "btn-outline-info";
    } else if (service.state.status === ApiServiceStatus.UNAVAILABLE) {
		iconName = "warning";
		text = "Serveur introuvable";
		className = "btn-outline-danger";
    }

	$: classes = className + (!$onProfilePage && $server?.id === service?.id ? " active" : "");

    async function setServer() {
        $server = service;
        if ($onProfilePage) {
            $onProfilePage = false;
            await goto(`/${$locale}/app/`);
        }
        await refreshSelectedServer();
    }
</script>

<SidebarItem {iconName} className={classes} onClick={setServer}>
    {text}
</SidebarItem>