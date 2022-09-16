<script lang="ts">
	import Sidebar from "$components/app/sidebar/Sidebar.svelte";
	import { token } from "$store/store";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import { refreshSelectedServer } from "$components/app/helpers";
	import Icons from "$components/icons/Icons.svelte";
	import "$root/app.scss";
	import { locale } from "svelte-intl-precompile";

	onMount(async () => {
		if (!$token) {
			await goto(`/${$locale}/login?redirect=/${$locale}/dashboard/`);
		}

		await refreshSelectedServer();
	});
</script>

<svelte:head>
	<title>Mes serveurs</title>
</svelte:head>

<Icons />

<div class="d-flex vh-100">
	<Sidebar />

	<div class="content overflow-auto flex-grow-1 p-3 d-flex flex-column gap-3">
		<slot />
	</div>
</div>

<style lang="scss">
	@include media-breakpoint-down(sm) {
		.content {
			margin-left: 74px;
		}
	}
</style>
