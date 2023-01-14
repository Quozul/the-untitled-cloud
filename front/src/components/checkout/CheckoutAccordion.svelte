<script lang="ts">
	import { CheckoutSteps } from "$components/checkout/constants";
	import LoginForm from "$components/login/LoginForm.svelte";
	import AddressForm from "$components/address/AddressForm.svelte";
	import { checkoutStep, token } from "$store/store";
	import StripeCheckout from "$components/checkout/StripeCheckout.svelte";
	import { t } from "svelte-intl-precompile";
	import AccordionItem from "$components/accordion/AccordionItem.svelte";
	import { cart } from "$store/store";

	$checkoutStep = $token ? CheckoutSteps.PROFILE : CheckoutSteps.LOGIN;

	let loginOpened: boolean;
	let addressOpened: boolean;
	let checkoutOpened: boolean;
	$: loginOpened = $checkoutStep === CheckoutSteps.LOGIN;
	$: addressOpened = $checkoutStep === CheckoutSteps.PROFILE;
	$: checkoutOpened = $checkoutStep === CheckoutSteps.CHECKOUT;

	function setProfileTab() {
		$checkoutStep = CheckoutSteps.PROFILE;
	}

	function setCheckoutTab() {
		$checkoutStep = CheckoutSteps.CHECKOUT;
	}

	function handleClick(event) {
		const { opened, id } = event.detail;

		if (opened) {
			$checkoutStep = id;
		}
	}
</script>

<div class="accordion accordion-flush border-top border-bottom">
	<AccordionItem
		name={$t("authentication.login")}
		on:click={handleClick}
		id={CheckoutSteps.LOGIN}
		bind:opened={loginOpened}
	>
		<LoginForm redirectTo={null} defaultStyle={false} on:submit={setProfileTab} />
	</AccordionItem>

	<AccordionItem
		name={$t("address.billing_address")}
		disabled={!$token}
		on:click={handleClick}
		id={CheckoutSteps.PROFILE}
		bind:opened={addressOpened}
	>
		<AddressForm on:submit={setCheckoutTab} />
		<small class="cursor-pointer text-muted" on:click={setCheckoutTab}>Skip (debug)</small>
	</AccordionItem>

	<AccordionItem
		name={$t("common.checkout")}
		disabled={!$token || $cart.length === 0}
		on:click={handleClick}
		id={CheckoutSteps.CHECKOUT}
		bind:opened={checkoutOpened}
	>
		<StripeCheckout />
	</AccordionItem>
</div>
