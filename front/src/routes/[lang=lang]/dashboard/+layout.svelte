<script lang="ts">
	import Sidebar from "$components/sidebar/Sidebar.svelte";
	import { token } from "$store/store";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import Icons from "$components/icons/Icons.svelte";
	import "$root/app.scss";
	import { locale, t } from "svelte-intl-precompile";
	import SessionExpiredModal from "$components/errors/SessionExpiredModal.svelte";

	onMount(async () => {
		if (!$token) {
			await goto(`/${$locale}/login?redirect=/${$locale}/dashboard/`);
		}
	});
</script>

<svelte:head>
	<title>{$t("common.my_servers")}</title>
</svelte:head>

<Icons />

<div class="d-flex position-fixed h-100 w-100">
	<Sidebar />

	<div class="overflow-auto flex-grow-1 d-flex flex-column gap-lg-3">
		<slot />
	</div>
</div>

<SessionExpiredModal />
