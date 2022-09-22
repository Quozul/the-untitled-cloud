<script lang="ts">
	import { onProfilePage, user, sidebarCollapsed } from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import { onDestroy, onMount } from "svelte";
	import { getUser } from "$components/app/helpers";
	import DeleteAccount from "$components/profile/DeleteAccount.svelte";
	import { t } from "svelte-intl-precompile";
	import DashboardNav from "$components/navbar/DashboardNav.svelte";

	onMount(async () => {
		const { response } = await getUser();
		$user = response;
		$sidebarCollapsed = true;
		$onProfilePage = true;
	});

	onDestroy(() => {
		$onProfilePage = false;
	})
</script>

<div class="m-lg-3 d-flex flex-column gap-lg-3">
	<DashboardNav>
		<span class="fw-bolder m-0 fs-5">
			{$t("common.profile")}
		</span>
	</DashboardNav>

	<div class="bg-light p-4 d-flex element flex-column">
		<p class="d-flex align-items-center gap-2 lead">
			<Icon key="tools" />
			{$t("profile.title")}
		</p>
		<p>
			{$t("profile.description")}
			<a href="mailto:contact@theuntitledcloud.com">
				contact@theuntitledcloud.com
			</a>
		</p>

		<div>
			<DeleteAccount />
		</div>
	</div>
</div>
