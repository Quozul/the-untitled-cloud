<script lang="ts">
	import { CheckoutSteps } from "./constants";
	import { checkoutStep } from "../../store/store";
	import { setStep } from "./helpers.js";

	export let step: CheckoutSteps;
</script>

<button
    class="nav-link"
    on:click={() => setStep(step)}
    class:active={$checkoutStep === step}
>
    <slot/>
</button>