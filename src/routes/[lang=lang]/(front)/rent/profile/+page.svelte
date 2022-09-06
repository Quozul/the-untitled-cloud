<script>
	import AddressForm from "$components/address/AddressForm.svelte";
	import { checkoutStep } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";

	$checkoutStep = CheckoutSteps.PROFILE;
</script>

<svelte:head>
    <title>Profil</title>
</svelte:head>

<AddressForm/>