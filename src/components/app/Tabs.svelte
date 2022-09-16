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

<nav class="nav nav-pills nav-fill flex-column flex-lg-row">
	<a
		class="nav-link"
		class:active={!$fetchingServer && $selectedTab === ServerTab.INFO}
		class:disabled={$fetchingServer || isEnded}
		href="/{$locale}/dashboard/"
	>
		Informations
	</a>

	{#if $server?.product.id === Products.MinecraftServer || $server?.product.id === Products.ArkServer}
		<a
			class="nav-link"
			class:active={!$fetchingServer && $selectedTab === ServerTab.PARAMETERS}
			class:disabled={$fetchingServer || isPending || isEnded || isSuspended}
			href="/{$locale}/dashboard/parameters/"
		>
			Paramètres
		</a>
	{/if}

	{#if $server?.product.id === Products.MinecraftServer}
		<a
			class="nav-link"
			class:active={!$fetchingServer && $selectedTab === ServerTab.CONSOLE}
			class:disabled={$fetchingServer || isPending || isEnded || isSuspended}
			href="/{$locale}/dashboard/console/"
		>
			Console
		</a>
	{/if}

	<Button
		className="nav-link justify-content-center"
		disabled={$fetchingServer}
		icon="box-arrow-up-right"
		onClick={showModal}
	>
		Abonnement
	</Button>
</nav>

<Modal bind:visible={modalVisible} okText="Gérer mes abonnements" onClick={redirectToStripe}>
	<div class="p-3">
		Vous allez être redirigé vers notre partenaire pour gérer vos abonnements.
	</div>
</Modal>
