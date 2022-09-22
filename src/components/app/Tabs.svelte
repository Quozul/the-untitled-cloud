<script lang="ts">
	import { fetchingServer, server } from "$store/store";
	import { ServerTab } from "$components/app/constants";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { t } from "svelte-intl-precompile";
	import { Products } from "$components/cart/constants";
	import Button from "$shared/Button.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import { getStripePortal } from "./helpers";
	import { page } from "$app/stores";
	import TabLink from "./TabLink.svelte";
	import type { ApiError } from "$shared/models";
	import Alert from "$shared/Alert.svelte";
	import { Variant } from "$shared/constants.js";

	let isPending: boolean;
	let isEnded: boolean;
	let isSuspended: boolean;
	let hasConsole: boolean;
	let modalVisible = false;
	let apiError: ApiError | null = null;

	$: {
		isPending = $server?.subscription.status === ApiSubscriptionStatus.PENDING;
		isEnded = $server?.subscription.status === ApiSubscriptionStatus.CANCELLED;
		isSuspended = $server?.subscription.status === ApiSubscriptionStatus.SUSPENDED;
		hasConsole = $server?.product.id === Products.MinecraftServer;
	}

	function showModal() {
		modalVisible = true;
	}

	async function redirectToStripe() {
		const redirect = $page.url.href;
		const { error, response } = await getStripePortal(redirect);

		if (response) {
			window.open(response.url);
			modalVisible = false;
		}

		apiError = error;
	}
</script>

<hr class="d-block d-lg-none m-0" />

<nav class="d-flex gap-lg-2">
	<TabLink
		active={!$fetchingServer && $page.routeId === ServerTab.INFO}
		className="justify-content-center w-100 border-0 py-3 p-lg-2"
		href="/"
		icon="info"
		outline={true}
	>
		<span class="d-none d-lg-inline">{$t("tabs.information")}</span>
	</TabLink>

	<TabLink
		active={!$fetchingServer && $page.routeId === ServerTab.PARAMETERS}
		className="justify-content-center w-100 border-0 py-3 p-lg-2"
		disabled={$fetchingServer || isPending || isEnded || isSuspended}
		href="/parameters/"
		icon="gear"
		outline={true}
	>
		<span class="d-none d-lg-inline">{$t("tabs.parameters")}</span>
	</TabLink>

	<TabLink
		active={!$fetchingServer && $page.routeId === ServerTab.FILES}
		className="justify-content-center w-100 border-0 py-3 p-lg-2"
		disabled={$fetchingServer || isPending || isEnded || isSuspended}
		href="/files/"
		icon="directory"
		outline={true}
	>
		<span class="d-none d-lg-inline">{$t("tabs.files")}</span>
	</TabLink>

	<TabLink
		active={!$fetchingServer && $page.routeId === ServerTab.CONSOLE}
		className="justify-content-center w-100 border-0 py-3 p-lg-2"
		disabled={$fetchingServer || isPending || isEnded || isSuspended || !hasConsole}
		href="/console/"
		icon="terminal"
		outline={true}
	>
		<span class="d-none d-lg-inline">{$t("tabs.console")}</span>
	</TabLink>

	<Button
		className="justify-content-center w-100 border-0 py-3 p-lg-2"
		disabled={$fetchingServer}
		icon="credit-card"
		onClick={showModal}
		outline={true}
	>
		<span class="d-none d-lg-inline">{$t("tabs.subscription")}</span>
	</Button>
</nav>

<Modal bind:visible={modalVisible} okText={$t("tabs.manage_subs")} onClick={redirectToStripe}>
	<div class="p-3">
		{$t("tabs.redirect_to_stripe")}

		{#if apiError}
			<Alert variant={Variant.DANGER} className="mt-3 mb-0">
				{apiError.translatedMessage || apiError.message}
			</Alert>
		{/if}
	</div>
</Modal>
