<script lang="ts">
	import { CheckoutSteps } from "$components/checkout/constants";
	import Cart from "$components/cart/Cart.svelte";
	import { checkoutStep } from "$store/store";
	import Steps from "$components/steps/Steps.svelte";
</script>

<div class="d-flex flex-column gap-5 my-5">
	<Steps />

	<div class="row">
		<Cart canEdit={$checkoutStep !== CheckoutSteps.CHECKOUT} />

		<div class="col-md-7 col-lg-8">
			<slot />
		</div>
	</div>
</div>
