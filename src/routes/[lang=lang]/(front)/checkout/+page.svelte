<script lang="ts">
	import { CheckoutSteps } from "$components/checkout/constants";
	import LoginForm from "$components/login/LoginForm.svelte";
	import Cart from "$components/cart/Cart.svelte";
	import AddressForm from "$components/address/AddressForm.svelte";
	import { checkoutStep, token } from "$store/store";
	import StripeCheckout from "$components/checkout/StripeCheckout.svelte";
	import { t } from "svelte-intl-precompile";

	$checkoutStep = $token ? CheckoutSteps.PROFILE : CheckoutSteps.LOGIN;

	function setLoginTab() {
		$checkoutStep = CheckoutSteps.LOGIN;
	}

	function setProfileTab() {
		$checkoutStep = CheckoutSteps.PROFILE;
	}

	function setCheckoutTab() {
		$checkoutStep = CheckoutSteps.CHECKOUT;
	}
</script>

<svelte:head>
	<title>{$t("checkout")}</title>
</svelte:head>

<div class="row">
	<div class="col">
		<ul class="list-group list-group-flush">
			<li class="list-group-item py-3 border-top">
				{#if $checkoutStep === CheckoutSteps.LOGIN}
					<LoginForm redirectTo={null} defaultStyle={false} on:submit={setProfileTab} />
				{:else}
					<div on:click={setLoginTab} class="cursor-pointer text-secondary fs-4">Connexion</div>
				{/if}
			</li>

			<li class="list-group-item py-3">
				{#if $checkoutStep === CheckoutSteps.PROFILE}
					<AddressForm on:submit={setCheckoutTab} />

					<small class="cursor-pointer text-muted" on:click={setCheckoutTab}>Skip (debug)</small>
				{:else}
					<div on:click={setProfileTab} class="cursor-pointer text-secondary fs-4">
						Adresse de facturation
					</div>
				{/if}
			</li>

			<li class="list-group-item py-3 border-bottom">
				{#if $checkoutStep === CheckoutSteps.CHECKOUT}
					<StripeCheckout />
				{:else}
					<div on:click={setCheckoutTab} class="cursor-pointer text-secondary fs-4 disabled">Paiement</div>
				{/if}
			</li>
		</ul>
	</div>

	<div class="col">
		<Cart canEdit={$checkoutStep !== CheckoutSteps.CHECKOUT} background={true} />
	</div>
</div>