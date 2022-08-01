<script lang="ts">
	import { onMount } from "svelte";
	import { checkoutStep, token } from "../../store/store.ts";
	import type { Address } from "./Address";
	import { CheckoutSteps } from "../checkout/constants";

	let address: Address = {
		city: null,
		country: null,
		fullname: null,
		line1: null,
		line2: null,
		postal_code: null,
		state: null,
	};
	let addAddress = false;

	onMount(() => {
		// Get address
		fetch(`${import.meta.env.VITE_API_BASE_URL}/user/address`, {
			method: "GET",
			headers: new Headers({
				"content-type": "application/json",
				"authorization": `Bearer ${$token}`,
			}),
		})
			.then(res => {
				if (res.status === 401) {
					$token = null; // Disconnect user
					$checkoutStep = CheckoutSteps.LOGIN;
				} else if (res.status === 204) {
					addAddress = true;
					return;
				}
				res.json()
					.then(json => {
						address = json;
						console.log(json);
					});
			});
	});

	function submit() {
		fetch(`${import.meta.env.VITE_API_BASE_URL}/user/address`, {
			method: "POST",
			headers: new Headers({
				"content-type": "application/json",
				"authorization": `Bearer ${$token}`,
			}),
            body: JSON.stringify(address),
		})
            .then(res => {
				if (res.ok) {
					$checkoutStep = CheckoutSteps.CHECKOUT;
                }
            });
	}
</script>

<h4 class="mb-3">Adresse de facturation</h4>

<form class="needs-validation" novalidate="" on:submit|preventDefault={submit}>
    <div class="row g-3">
        <div class="col-sm-12">
            <label for="firstName" class="form-label">Nom complet</label>
            <input type="text" class="form-control" id="firstName" placeholder="" required="" bind:value={address.fullname}>
            <div class="invalid-feedback">
                Requis.
            </div>
        </div>

        <div class="col-12">
            <label for="address" class="form-label">Adresse</label>
            <input type="text" class="form-control" id="address" placeholder="Adresse" required="" bind:value={address.line1}>
            <div class="invalid-feedback">
                Requis.
            </div>
        </div>

        <div class="col-12">
            <label for="address2" class="form-label">Complément d'adresse <span
                    class="text-muted">(Optionnel)</span></label>
            <input type="text" class="form-control" id="address2" placeholder="Complément d'adresse" bind:value={address.line2}>
        </div>

        <div class="col-md-5">
            <label for="country" class="form-label">Pays</label>
            <select class="form-select" id="country" required="" bind:value={address.country}>
                <option value="FR" selected>France</option>
            </select>
            <div class="invalid-feedback">
                Requis.
            </div>
        </div>

        <div class="col-md-4">
            <label for="city" class="form-label">Ville</label>
            <input type="text" class="form-control" name="city" id="city" placeholder="Paris" bind:value={address.city}>
            <div class="invalid-feedback">
                Requis.
            </div>
        </div>

        <div class="col-md-3">
            <label for="zip" class="form-label">Code postal</label>
            <input type="text" class="form-control" id="zip" placeholder="75007" required="" bind:value={address.postal_code}>
            <div class="invalid-feedback">
                Requis.
            </div>
        </div>
    </div>

    <button class="btn btn-primary mt-3">Ajouter l'adresse</button>
    <button class="btn btn-secondary mt-3" type="button" on:click|preventDefault={() => $checkoutStep = CheckoutSteps.CHECKOUT}>Passer >></button>
</form>