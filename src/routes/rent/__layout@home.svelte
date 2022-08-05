<script lang="ts">
	import { CheckoutSteps } from "../../components/checkout/constants";
	import Step from "../../components/checkout/Step.svelte";
	import Cart from "../../components/Cart.svelte";
	import { cart } from "../../store/store";
	import { token } from "../../store/store";
    import { t } from "svelte-intl-precompile";
</script>

<nav class="nav nav-pills nav-fill mb-3">
    <Step step={CheckoutSteps.PRODUCTS}>
        {$t("choose_product")}
    </Step>
    <Step step={CheckoutSteps.LOGIN} disabled="{!!$token}">
        {$t("to_login")}
    </Step>
    <Step step={CheckoutSteps.PROFILE} disabled="{!$token}">
        {$t("complete_profile")}
    </Step>
    <Step step={CheckoutSteps.CHECKOUT} disabled="{!$token || !$cart}">
        {$t("checkout")}
    </Step>
</nav>

<div class="row g-5">
    <Cart/>

    <div class="col-md-7 col-lg-8">
        <slot/>
    </div>
</div>
