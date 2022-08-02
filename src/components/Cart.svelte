<script lang="ts">
	import type { Unsubscriber } from "svelte/store";
	import { cart } from "../store/store";
	import { formatPrice } from "../shared/helpers";
	import Icon from "./icons/Icon.svelte";
	import { onDestroy, onMount } from "svelte";

	let unsubscribe: Unsubscriber = null;
	let total: number = $cart?.price ?? 0;

	onMount(() => {
		unsubscribe = cart.subscribe(value => {
			total = value?.price ?? 0;
        });
    });

	onDestroy(() => {
		unsubscribe?.();
    });

	function removeFromCart() {
		$cart = null;
    }
</script>

<div class="col-md-5 col-lg-4 order-md-last">
    <h4 class="d-flex justify-content-between align-items-center mb-3">
        <span class="text-primary">Panier</span>
    </h4>
    <ul class="list-group mb-3">
        {#if $cart}
            <li class="list-group-item d-flex justify-content-between lh-sm">
                <div>
                    <div class="d-flex align-items-center">
                        <Icon key="x-lg" onClick={removeFromCart}/>
                        <h6 class="my-0">{$cart.name}</h6>
                    </div>
                    <small class="text-muted">{$cart.description}</small>
                </div>
                <span class="text-muted">{formatPrice($cart.price)}</span>
            </li>
        {:else}
            <li class="list-group-item d-flex justify-content-between lh-sm">
                <div>
                    <h6 class="my-0">Vous n'avez rien dans votre panier.</h6>
                </div>
            </li>
        {/if}
        <li class="list-group-item d-flex justify-content-between">
            <span>Total (EUR)</span>
            <strong>{formatPrice(total)}</strong>
        </li>
    </ul>
</div>