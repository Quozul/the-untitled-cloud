<script lang="ts">
	import type { Invoice } from "../../models";
	import { ZonedDateTime } from "@js-joda/core";
	import { shortDate } from "../../../../shared/constants";
	import { formatPrice } from "../../../../shared/helpers.js";
	import Button from "../../../shared/Button.svelte";

	// Props
	export let invoice: Invoice;

	// State
	let currentPeriodStart: string;
	let currentPeriodEnd: string;

	$: {
		currentPeriodStart = invoice?.periodStart && ZonedDateTime.parse(invoice?.periodStart).format(shortDate);
		currentPeriodEnd = invoice?.periodEnd && ZonedDateTime.parse(invoice?.periodEnd).format(shortDate);
	}

	async function downloadInvoice() {

	}
</script>

<style>
	div {
		max-width: 330px;
	}
</style>

<div class="border p-3 d-flex flex-column align-items-start gap-3">
	<dl class="d-flex flex-column m-0 w-100">
		<div class="separation">
			{#if !invoice}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12"></span>
				</p>
			{:else}
				<dt>Pour la période</dt>
				<dd>du <u>{currentPeriodStart}</u> au <u>{currentPeriodEnd}</u></dd>
			{/if}
		</div>

		<div class="separation">
			{#if !invoice}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12"></span>
				</p>
			{:else}
				<dt>Montant du</dt>
				<dd>{formatPrice(invoice.amountDue, invoice.currency)}</dd>
			{/if}
		</div>

		<div class="separation">
			{#if !invoice}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12"></span>
				</p>
			{:else}
				<dt>Montant payé</dt>
				<dd>{formatPrice(invoice.amountPaid, invoice.currency)}</dd>
			{/if}
		</div>

		<div class="separation">
			{#if !invoice}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12"></span>
				</p>
			{:else}
				<dt>Reste à payer</dt>
				<dd>{formatPrice(invoice.amountRemaining, invoice.currency)}</dd>
			{/if}
		</div>
	</dl>

	<Button onClick={downloadInvoice}>
		Télécharger
	</Button>
</div>