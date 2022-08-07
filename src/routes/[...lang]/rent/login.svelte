<script lang="ts">
	import LoginForm from "$components/login/LoginForm.svelte";
	import { checkoutStep, cart } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";

	$checkoutStep = CheckoutSteps.LOGIN;
</script>

<svelte:head>
    <title>Connexion</title>
</svelte:head>

<LoginForm redirectTo={$cart ? "/rent/profile/" : "/rent/products/"}/>