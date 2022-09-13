<script>
	import Step from "$components/steps/Step.svelte";
	import { CheckoutSteps } from "$components/checkout/constants";
	import { checkoutStep } from "$store/store";
</script>

<div class="row w-100 g-2">
	<Step href="/rent/products/" current={$checkoutStep >= CheckoutSteps.PRODUCTS} title="Étape 1" description="Sélection du service" />
	<Step href="/rent/login/" current={$checkoutStep >= CheckoutSteps.LOGIN} title="Étape 2" description="Connexion" />
	<Step href="/rent/profile/" current={$checkoutStep >= CheckoutSteps.PROFILE} title="Étape 3" description="Confirmation des informations" />
	<Step href="/rent/checkout/" current={$checkoutStep >= CheckoutSteps.CHECKOUT} title="Étape 4" description="Paiement" />
</div>
