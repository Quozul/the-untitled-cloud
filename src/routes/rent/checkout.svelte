<script>
	import CheckoutElement from "/src/components/checkout/CheckoutElement.svelte";
	import { checkoutStep } from "/src/store/store";
	import { CheckoutSteps } from "/src/components/checkout/constants";

	$checkoutStep = CheckoutSteps.CHECKOUT;
</script>

<CheckoutElement/>