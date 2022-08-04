<script lang="ts">
	import type { Address } from "./Address";
	import type { ApiError } from "../../shared/models";
	import { onMount } from "svelte";
	import { token } from "../../store/store.ts";
	import { CheckoutSteps } from "../checkout/constants";
	import Icon from "../icons/Icon.svelte";
	import { setStep } from "../checkout/helpers";
	import jwtDecode from "jwt-decode";
	import { getAddress, setAddress } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";

	let address: Address = {
		city: null,
		country: null,
		fullname: null,
		line1: null,
		line2: null,
		postal_code: null,
		state: null,
	};
	let email = "";

	onMount(async () => {
        const jwt = jwtDecode($token);
		email = jwt.email;

		try {
			const response = await getAddress();

			// If the user has an address
			if (response) {
				address = response;
            }
		} catch (e: ApiError) {
			// TODO: Handle error
            console.log(e);
		}
	});

	async function submit() {
		try {
            await setAddress(address);
			await setStep(CheckoutSteps.CHECKOUT);
		} catch (e: ApiError) {
			// TODO: Handle error
			console.log(e);
		}
	}

	async function skip() {
        await setStep(CheckoutSteps.CHECKOUT);
	}
</script>

<h4 class="mb-1">Adresse de facturation</h4>

<div class="fs-6 mb-3">
    Connecté en tant que&nbsp;
    <div class="text-muted d-inline">
        {email}
    </div>
</div>

<form>
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

    <Button type="submit" className="btn btn-primary mt-3" onClick={submit}>
        Ajouter l'adresse
    </Button>

    <button class="btn btn-secondary mt-3 d-inline-flex align-items-center" type="button" on:click|preventDefault={skip}>
        Passer
        <Icon key="chevron-double-right" className="ms-2"/>
    </button>
</form>