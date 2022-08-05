<script>
	import LoginForm from "$components/login/LoginForm.svelte";
	import { token, checkoutStep } from "$store/store";
	import { CheckoutSteps } from "$components/checkout/constants";

	$checkoutStep = CheckoutSteps.LOGIN;
</script>

<svelte:head>
    <title>Connexion</title>
</svelte:head>

<LoginForm redirectTo="/rent/profile/"/>