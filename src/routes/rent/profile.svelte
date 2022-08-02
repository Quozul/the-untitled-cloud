<script>
	import AddressForm from "/src/components/address/AddressForm.svelte";
	import { checkoutStep } from "/src/store/store";
	import { CheckoutSteps } from "/src/components/checkout/constants";

	$checkoutStep = CheckoutSteps.PROFILE;
</script>

<AddressForm/>