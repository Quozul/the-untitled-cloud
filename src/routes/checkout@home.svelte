<script lang="ts">
	import AddressForm from "../components/address/AddressForm.svelte";
	import Cart from "../components/Cart.svelte";
	import { checkoutStep, token } from "../store/store.ts";
	import jwtDecode from "jwt-decode";
	import LoginForm from "../components/login/LoginForm.svelte";
	import CheckoutElement from "../components/checkout/CheckoutElement.svelte";
	import { CheckoutSteps } from "../components/checkout/constants";

	if (!!$token) {
		// Check token expiry
		const decoded = jwtDecode($token);
		if (Date.now() / 1000 > decoded.exp) {
			// Token is expired
			$token = "";
			$checkoutStep = CheckoutSteps.LOGIN; // Ask for logging
		} else {
			$checkoutStep = CheckoutSteps.PROFILE;
        }
	}

	function setStep(newStep: string): void {
		$checkoutStep = newStep;
	}
</script>

<nav class="nav nav-pills nav-fill mb-3">
    <button
        class="nav-link"
        on:click={() => setStep(CheckoutSteps.LOGIN)}
        class:active={$checkoutStep === CheckoutSteps.LOGIN}
        disabled="{$checkoutStep === CheckoutSteps.COMPLETE || !!$token}"
    >
        Se connecter
    </button>

    <button
        class="nav-link"
        on:click={() => setStep(CheckoutSteps.PROFILE)}
        class:active={$checkoutStep === CheckoutSteps.PROFILE}
        disabled="{$checkoutStep === CheckoutSteps.COMPLETE}"
    >
        Compléter son profil
    </button>

    <button
        class="nav-link"
        on:click={() => setStep(CheckoutSteps.CHECKOUT)}
        class:active={$checkoutStep === CheckoutSteps.CHECKOUT}
        disabled="{$checkoutStep === CheckoutSteps.COMPLETE}"
    >
        Paiement
    </button>

    <button
        class="nav-link"
        disabled
        class:active={$checkoutStep === CheckoutSteps.COMPLETE}
    >
        Complétion
    </button>
</nav>

<div class="row g-5">
    <Cart/>

    <div class="col-md-7 col-lg-8">
        {#if $checkoutStep === CheckoutSteps.LOGIN}
            <LoginForm/>
        {:else if $checkoutStep === CheckoutSteps.PROFILE}
            <AddressForm/>
        {:else if $checkoutStep === CheckoutSteps.CHECKOUT}
            <CheckoutElement/>
        {/if}
    </div>
</div>

