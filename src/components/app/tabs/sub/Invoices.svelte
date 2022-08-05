<script lang="ts">
	import type { SubscriptionInfo } from "$components/app/models";
	import Invoice from "./Invoice.svelte";

	export let subscription: SubscriptionInfo;
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-stretch">
	<h4>Factures</h4>
	<Invoice invoice={subscription?.latestInvoice}/>
</div>