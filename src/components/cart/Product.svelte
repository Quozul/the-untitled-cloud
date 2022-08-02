<script lang="ts">
	import { cart } from "../../store/store";
	import { setStep } from "../checkout/helpers";
	import { CheckoutSteps } from "../checkout/constants";

	function addToCart() {
		$cart = {
			id: "",
			price: 200,
			name: "Serveur Minecraft",
			description: "Starter",
		}

		setStep(CheckoutSteps.LOGIN);
	}
</script>

<div class="card">
    <div class="card-header py-4">
        <h4 class="my-0 fw-normal">Starter</h4>
    </div>

    <div class="card-body">
        <p class="mb-0 text-muted fw-light">à partir de</p>
        <h1 class="card-title pricing-card-title">2€<small class="text-muted fw-light">/mois</small>
        </h1>

        <ul class="list-unstyled mt-3">
            <li>2 Go de mémoire vive</li>
            <li>1 coeur CPU</li>
        </ul>

        <button on:click|preventDefault={addToCart} class="w-100 btn btn-lg btn-outline-primary">
            Choisir
        </button>
    </div>
</div>