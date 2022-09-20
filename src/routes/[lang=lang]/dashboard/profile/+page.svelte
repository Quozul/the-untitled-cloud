<script lang="ts">
	import { onProfilePage, server, user } from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import Button from "$shared/Button.svelte";
	import { onMount } from "svelte";
	import { getUser } from "$components/app/helpers";
	import DeleteAccount from "$components/profile/DeleteAccount.svelte";
	import { t } from "svelte-intl-precompile";
	import { toggleSidebarCollapsed } from "$components/sidebar/helpers.js";
	import { Variant } from "$shared/constants.js";
	import { sidebarCollapsed } from "$store/store.js";

	$server = null;
	$onProfilePage = true;

	onMount(async () => {
		const { response } = await getUser();
		$user = response;
		$sidebarCollapsed = true;
	});
</script>

<div class="m-lg-3 d-flex flex-column gap-lg-3">
	<div class="order-0 order-lg-0">
		<div class="bg-light py-3 d-flex align-items-center gap-1 gap-lg-3 px-3 justify-content-between">
			<div class="sidebar-header d-flex align-items-center gap-2">
				<Button
					icon="list"
					onClick={toggleSidebarCollapsed}
					variant={Variant.LIGHT}
					className="d-inline d-lg-none"
				/>
				<span class="fw-bolder m-0 fs-5">{$t("common.profile")}</span>
			</div>
		</div>
	</div>

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
