<script lang="ts">
	import { CheckoutSteps } from "$components/checkout/constants";
	import Cart from "../../../components/cart/Cart.svelte";
	import { cart, token, checkoutStep } from "$store/store";
	import { t } from "svelte-intl-precompile";
	import Link from "$components/shared/Link.svelte";
	import { classNames } from "$shared/helpers";
</script>

<nav class="nav nav-pills nav-fill mb-3">
	<Link
		href="/rent/products/"
		className={classNames({
            "nav-link": true,
            active: $checkoutStep === CheckoutSteps.PRODUCTS,
        })}
	>
		1. {$t("choose_product")}
	</Link>

	<Link
		href="/rent/login/"
		className={classNames({
            "nav-link": true,
            active: $checkoutStep === CheckoutSteps.LOGIN,
            disabled: !!$token,
        })}
	>
		2. {$t("to_login")}
	</Link>

	<Link
		href="/rent/profile/"
		className={classNames({
            "nav-link": true,
            active: $checkoutStep === CheckoutSteps.PROFILE,
            disabled: !$token
        })}
	>
		3. {$t("complete_profile")}
	</Link>

	<Link
		href="/rent/checkout/"
		className={classNames({
            "nav-link": true,
            active: $checkoutStep === CheckoutSteps.CHECKOUT,
            disabled: !$token || !$cart
        })}
	>
		4. {$t("checkout")}
	</Link>
</nav>

<div class="row g-5">
	<Cart canEdit={$checkoutStep !== CheckoutSteps.CHECKOUT}/>

	<div class="col-md-7 col-lg-8">
		<slot/>
	</div>
</div>
