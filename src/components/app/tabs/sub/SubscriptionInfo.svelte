<script lang="ts">
	import type { Server, SubscriptionInfo } from "../../models";
	import { ZonedDateTime } from "@js-joda/core";
	import Button from "../../../shared/Button.svelte";
	import {
		cancelSubscription,
		getSubscription,
		toggleRefreshServerInfo,
		toggleRefreshServerList
	} from "../../helpers";
	import { selectedServer } from "../../../../store/store";
	import type { ApiError } from "../../../shared/models";
	import { ButtonVariant } from "../../../shared/constants";
	import { formatter, shortDate } from "../../../shared/constants";
	import Invoices from "./Invoices.svelte";

	// State
	let subscription: SubscriptionInfo | null = null;
	let error: ApiError = null;

	let startDate: string;
	let currentPeriodStart: string;
	let currentPeriodEnd: string;
	let canceledAt: string;

	async function fetchSubscription(server: Server = $selectedServer) {
		try {
			subscription = null;
			subscription = await getSubscription(server.id);

			startDate = subscription?.startDate && ZonedDateTime.parse(subscription.startDate).format(formatter);
			currentPeriodStart = subscription?.currentPeriodStart && ZonedDateTime.parse(subscription.currentPeriodStart).format(shortDate);
			currentPeriodEnd = subscription?.currentPeriodEnd && ZonedDateTime.parse(subscription.currentPeriodEnd).format(shortDate);
			canceledAt = subscription?.canceledAt && ZonedDateTime.parse(subscription.canceledAt).format(formatter);
		} catch (e: ApiError) {
			error = e;
		}
	}

	$: fetchSubscription($selectedServer);

	async function cancel() {
		try {
			await cancelSubscription($selectedServer.id)
			toggleRefreshServerInfo()
			toggleRefreshServerList()
		} catch (e: ApiError) {
			error = e;
		}
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-stretch">
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

	<hr/>

	<h5>Actions</h5>

	{#if !subscription || !subscription.canceledAt}
		<div class="d-flex flex-wrap gap-3">
			<Button loading={!subscription} variant={ButtonVariant.DANGER} onClick={cancel}>
				Annuler son abonnement
			</Button>
		</div>
	{/if}
</div>

<Invoices {subscription}/>