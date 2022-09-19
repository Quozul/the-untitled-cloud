<script lang="ts">
	import { fetchingServer, selectedTab, server } from "$store/store";
	import { ServerTab } from "$components/app/constants";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
	import { locale } from "svelte-intl-precompile";
	import { Products } from "$components/cart/constants";
	import Button from "$shared/Button.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import { getStripePortal } from "./helpers";
	import { page } from "$app/stores";
	import Icon from "$components/icons/Icon.svelte";
	import ButtonLink from "$shared/ButtonLink.svelte";
	import { Variant } from "$shared/constants.js";

	let isPending: boolean;
	let isEnded: boolean;
	let isSuspended: boolean;
	let modalVisible = false;

	$: {
		isPending = $server?.subscription.status === ApiSubscriptionStatus.PENDING;
		isEnded = $server?.subscription.status === ApiSubscriptionStatus.CANCELLED;
		isSuspended = $server?.subscription.status === ApiSubscriptionStatus.SUSPENDED;
	}

	function showModal() {
		modalVisible = true;
	}

	async function redirectToStripe() {
		const redirect = $page.url.href;
		const { response } = await getStripePortal(redirect);
		window.open(response.url);
		modalVisible = false;
	}
</script>

<nav class="d-flex gap-lg-2">
	<ButtonLink
		active={!$fetchingServer && $selectedTab === ServerTab.INFO}
		className="justify-content-center w-100 border-0"
		disabled={$fetchingServer || isEnded}
		href="/{$locale}/dashboard/"
		icon="info"
		outline={true}
	>
		<span class="d-none d-lg-inline">Informations</span>
	</ButtonLink>

	{#if $server?.product.id === Products.MinecraftServer || $server?.product.id === Products.ArkServer}
		<ButtonLink
			active={!$fetchingServer && $selectedTab === ServerTab.FILES}
			className="justify-content-center w-100 border-0"
			disabled={$fetchingServer || isPending || isEnded || isSuspended}
			href="/{$locale}/dashboard/files/"
			icon="directory"
			outline={true}
		>
			<span class="d-none d-lg-inline">Fichiers</span>
		</ButtonLink>
	{/if}

	{#if $server?.product.id === Products.MinecraftServer}
		<ButtonLink
			active={!$fetchingServer && $selectedTab === ServerTab.CONSOLE}
			className="justify-content-center w-100 border-0"
			disabled={$fetchingServer || isPending || isEnded || isSuspended}
			href="/{$locale}/dashboard/console/"
			icon="terminal"
			outline={true}
		>
			<span class="d-none d-lg-inline">Console</span>
		</ButtonLink>
	{/if}

	<Button
		className="justify-content-center w-100 border-0"
		disabled={$fetchingServer}
		icon="credit-card"
		onClick={showModal}
		outline={true}
	>
		<span class="d-none d-lg-inline">Abonnement</span>
	</Button>
</nav>

<Modal bind:visible={modalVisible} okText="Gérer mes abonnements" onClick={redirectToStripe}>
	<div class="p-3">
		Vous allez être redirigé vers notre partenaire pour gérer vos abonnements.
	</div>
</Modal>
