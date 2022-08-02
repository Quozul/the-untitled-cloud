<script lang="ts">
	import { CheckoutSteps } from "./constants";
	import { checkoutStep } from "../../store/store";
	import { setStep } from "./helpers.js";

	export let step: CheckoutSteps;
	export let disabled: boolean = false;
</script>

<button
    class="nav-link"
    on:click={() => setStep(step)}
    class:active={$checkoutStep === step}
    {disabled}
>
    <slot/>
</button>