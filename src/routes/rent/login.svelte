<script>
	import LoginForm from "/src/components/login/LoginForm.svelte";
	import { token, checkoutStep } from "/src/store/store";
	import { CheckoutSteps } from "/src/components/checkout/constants";

	$checkoutStep = CheckoutSteps.LOGIN;
</script>

<svelte:head>
    <title>Connexion</title>
</svelte:head>

<LoginForm redirectTo="/rent/profile/"/>