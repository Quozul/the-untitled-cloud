<script lang="ts">
	import { page } from "$app/stores";
	import { getStripePortal } from "$components/app/helpers";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";
	import { t } from "svelte-intl-precompile";

	async function redirectToStripe() {
		const redirect = $page.url.href;
		const { response } = await getStripePortal(redirect);
		window.open(response.url);
	}
</script>

<div class="bg-light p-4 d-flex element flex-column align-items-start">
	<h4>{$t("server_status.suspended")}</h4>

	<p class="lead">{$t("dashboard_errors.suspended.description")}</p>

	<Button icon="box-arrow-up-right" onClick={redirectToStripe} variant={Variant.SECONDARY}>
		{$t("dashboard_errors.suspended.cta")}
	</Button>
</div>
