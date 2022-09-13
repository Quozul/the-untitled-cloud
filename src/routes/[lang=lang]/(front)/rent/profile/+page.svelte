<script>
	import AddressForm from "$components/address/AddressForm.svelte";
	import { cart, checkoutStep } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";
	import { goto } from "$app/navigation";
	import { locale } from "svelte-intl-precompile";

	$checkoutStep = CheckoutSteps.PROFILE;

	async function handleSubmit() {
		await goto($cart ? `/${$locale}/rent/checkout/` : `/${$locale}/products/`);
	}
</script>

<svelte:head>
	<title>Profil</title>
</svelte:head>

<AddressForm on:submit={handleSubmit} />
