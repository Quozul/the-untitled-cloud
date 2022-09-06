<script lang="ts">
	import { ZonedDateTime } from "@js-joda/core";
	import Button from "$shared/Button.svelte";
	import {
		cancelSubscription,
		getSubscriptionDetails,
		getSubscriptionProducts,
		refreshAllServers,
		refreshSelectedServer
	} from "$components/app/helpers";
	import { server } from "$store/store";
	import type { ApiError } from "$shared/models";
	import { Variant } from "$shared/constants";
	import { formatter, shortDate } from "$shared/constants";
	import Invoices from "./Invoices.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import InternalError from "$components/app/tabs/info/errors/InternalError.svelte";
	import type { ApiService } from "$models/ApiService";
	import type { ApiSubscriptionDetails } from "$models/ApiSubscriptionDetails";
	import type { ApiPaginate } from "$models/ApiPaginate";
	import type { ApiProduct } from "$models/ApiProduct";

	// State
	let subscription: ApiSubscriptionDetails | null = null;
	let error: ApiError = null;
	let modalVisible: boolean = false;
	let cancelError: ApiError = null;
	let products: ApiPaginate<ApiProduct> = null;

	let startDate: string;
	let currentPeriodStart: string;
	let currentPeriodEnd: string;
	let canceledAt: string;

	async function fetchSubscription(s: ApiService = $server) {
		try {
			products = null;
			subscription = null;
			subscription = await getSubscriptionDetails(s);
			products = await getSubscriptionProducts(s);

			startDate = subscription?.startDate && ZonedDateTime.parse(subscription.startDate).format(formatter);
			currentPeriodStart = subscription?.currentPeriodStart && ZonedDateTime.parse(subscription.currentPeriodStart).format(shortDate);
			currentPeriodEnd = subscription?.currentPeriodEnd && ZonedDateTime.parse(subscription.currentPeriodEnd).format(shortDate);
			canceledAt = subscription?.canceledAt && ZonedDateTime.parse(subscription.canceledAt).format(formatter);
		} catch (e: ApiError) {
			error = e;
		}
	}

	$: fetchSubscription($server);

	function openModal() {
		modalVisible = true;
	}

	async function cancel() {
		try {
			cancelError = null;
			await cancelSubscription($server);
			modalVisible = false;

			// Toggle global refresh
			fetchSubscription();
			refreshSelectedServer();
			refreshAllServers();
		} catch (e: ApiError) {
			cancelError = e;
			// TODO: Display error message somewhere
		}
	}
</script>

{#if !error}
	<div class="d-flex gap-3 flex-column flex-xl-row">
		<div class="flex-grow-1 bg-light p-4 d-flex element flex-column align-items-stretch">
			<h4>Votre abonnement</h4>

			<p class="text-muted">
				Informations concernant votre abonnement.
			</p>

			<dl class="d-flex flex-column m-0">
				<div class="separation">
					{#if !subscription}
						<p class="placeholder-glow w-100 m-0">
							<span class="placeholder h-100 col-12"></span>
						</p>
					{:else}
						<dt>État</dt>
						<dd>
							{#if !subscription.canceledAt}
								{subscription.status}
							{:else}
								Annulé le {canceledAt}
							{/if}
						</dd>
					{/if}
				</div>

				<div class="separation">
					{#if !subscription}
						<p class="placeholder-glow w-100 m-0">
							<span class="placeholder h-100 col-12"></span>
						</p>
					{:else}
						<dt>Commencé le</dt>
						<dd>{startDate}</dd>
					{/if}
				</div>

				<div class="separation">
					{#if !subscription}
						<p class="placeholder-glow w-100 m-0">
							<span class="placeholder h-100 col-12"></span>
						</p>
					{:else}
						<dt>Période actuelle</dt>
						<dd>du <u>{currentPeriodStart}</u> au <u>{currentPeriodEnd}</u></dd>
					{/if}
				</div>

				<div class="separation">
					{#if !subscription}
						<p class="placeholder-glow w-100 m-0">
							<span class="placeholder h-100 col-12"></span>
						</p>
					{:else}
						<dt>Moyen de paiement</dt>
						<dd>{subscription.paymentMethodType} {subscription.paymentMethodLast4}</dd>
					{/if}
				</div>
			</dl>

			{#if subscription && !subscription.canceledAt}
				<hr/>
				<h5>Actions</h5>

				<div class="d-flex flex-wrap gap-3">
					<Button
							loading={!subscription}
							variant={Variant.DANGER}
							onClick={openModal}
							disabled="{!subscription.latestInvoice.paid}">
						Annuler mon abonnement
					</Button>

					{#if subscription.latestInvoice.paid}
						<Modal
								bind:visible={modalVisible}
								onClick={cancel}
								title="Annulation"
								okText="Annuler mon abonnement"
								closeText="Annuler"
								variant={Variant.DANGER}
						>
							<p>
								Vous êtes sur le point de d'annuler votre abonnement.
								Dès que nous recevrons la confirmation de votre désabonnement, les fichiers de votre
								serveur
								seront
								immédiatement supprimés et ne seront pas récupérables.
							</p>
							<p>
								Vous serez remboursé au prorata de votre consommation.
							</p>
							<p>
								Êtes-vous sûr de vouloir continuer ?
							</p>
						</Modal>
					{:else}
						Vous ne pouvez pas annuler votre abonnement pour le moment.
						La dernière facture a été émise mais n'a pas encore été payée.
					{/if}
				</div>
			{/if}
		</div>

		<div class="flex-grow-1 bg-light p-4 d-flex element flex-column align-items-stretch">
			<h4>Produits</h4>

			<p class="text-muted">
				Cet abonnement concerne {products?.totalElements ?? ""} produit(s).
			</p>

			<dl class="d-flex flex-column m-0">
				{#if products?.data}
					{#each products.data as product}
						<div class="separation">
							<dt>{product.name}</dt>
							<dd>{product.description}</dd>
						</div>
					{/each}
				{:else}
					<p class="placeholder-glow w-100 m-0">
						<span class="placeholder h-100 col-12"></span>
					</p>
				{/if}
			</dl>
		</div>
	</div>

	<Invoices {subscription}/>
{:else}
	<InternalError refresh={fetchSubscription}/>
{/if}