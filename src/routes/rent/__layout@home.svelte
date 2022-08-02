<script lang="ts">
	import { CheckoutSteps } from "../../components/checkout/constants";
	import Step from "../../components/checkout/Step.svelte";
	import Cart from "../../components/Cart.svelte";
	import { cart } from "../../store/store";
	import { token } from "../../store/store.js";
</script>

<nav class="nav nav-pills nav-fill mb-3">
    <Step step={CheckoutSteps.PRODUCTS}>
        Choisir le produit
    </Step>
    <Step step={CheckoutSteps.LOGIN} disabled="{!!$token}">
        Se connecter
    </Step>
    <Step step={CheckoutSteps.PROFILE} disabled="{!$token}">
        Compléter son profil
    </Step>
    <Step step={CheckoutSteps.CHECKOUT} disabled="{!$cart}">
        Paiement
    </Step>
</nav>


<div class="row g-5">
    <Cart/>

    <div class="col-md-7 col-lg-8">
        <slot/>
    </div>
</div>
