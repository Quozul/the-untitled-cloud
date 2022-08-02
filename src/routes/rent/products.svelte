<script lang="ts">
	import { cart, checkoutStep } from "../../store/store";
	import { CheckoutSteps } from "../../components/checkout/constants.js";
	import { setStep } from "../../components/checkout/helpers";

	$checkoutStep = CheckoutSteps.PRODUCTS;

	function addToCart() {
        $cart = {
			id: "",
			price: 200,
			name: "Serveur Minecraft",
			description: "Illimité",
        }

		setStep(CheckoutSteps.LOGIN);
	}
</script>

<div class="row justify-content-center g-3">
    <div class="col-6">
        <div class="card">
            <div class="card-header py-4">
                <h4 class="my-0 fw-normal">Illimité<sup>*</sup></h4>
            </div>

            <div class="card-body">
                <p class="mb-0 text-muted fw-light">à partir de</p>
                <h1 class="card-title pricing-card-title">2€<small class="text-muted fw-light">/mois</small>
                </h1>

                <ul class="list-unstyled mt-3">
                    <li>Mémoire illimité<sup>*</sup></li>
                    <li>Stockage NVME illimité<sup>*</sup></li>
                    <li>Bande passante illimité<sup>*</sup></li>
                    <li>Support 7j/7</li>
                </ul>

                <div class="mt-3 mb-4">
                    <small>*Voir les <a href="#">CGU</a></small>
                </div>

                <button on:click|preventDefault={addToCart} class="w-100 btn btn-lg btn-outline-primary">
                    Choisir
                </button>
            </div>
        </div>
    </div>
</div>