<script lang="ts">
	import { CheckoutSteps } from "./constants";
    import { cart, checkoutStep, token } from "$store/store";
    import { locale } from "svelte-intl-precompile";
    import { href } from "$shared/helpers";

	export let step: CheckoutSteps;
	export let disabled: boolean = false;

    let link: string;

    function getStepLink(): string {
        if (step === CheckoutSteps.LOGIN) {
            if (!!$token) {
                return href(`/rent/${CheckoutSteps.PROFILE}/`, $locale as string);
            }
        }

        if (step === CheckoutSteps.CHECKOUT || step === CheckoutSteps.PROFILE) {
            if (!$token) {
                return href(`/rent/${CheckoutSteps.LOGIN}/`, $locale as string);
            }
        }

        if (step === CheckoutSteps.CHECKOUT) {
            if (!$cart) {
                return href(`/rent/${CheckoutSteps.PRODUCTS}/`, $locale as string);
            }
        }

        return href(`/rent/${step}/`, $locale as string);
    }

    $: {
        link = getStepLink();
    }
</script>

<a
    class="nav-link {disabled && 'disabled'}"
    {href}
    class:active={$checkoutStep === step}
>
    <slot/>
</a>