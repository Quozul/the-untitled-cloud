<script lang="ts">
	import type { ApiError } from "../../shared/models";
	import { redirect, signUp } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { credentials, loginMode } from "../../store/store";
	import { LoginMode } from "./models/LoginMode";

	// Props
	export let redirectTo: string;

	// Input fields
	let email = "";
	let password = "";
	let confirmPassword = null;
	let tos = false;

	// States
	let error = false;
	let errorMessage = null;

	async function submit() {
		if (password !== confirmPassword) {
			error = true;
			return;
		}

		try {
			error = false;
			const res = await signUp(email, password, "fr", tos);
			if (res.code === AuthenticationErrors.VERIFY_ACCOUNT) {
				$credentials = {email, password};
				$loginMode = LoginMode.VERIFICATION;
			} else {
				// This should never happen, but just in case...
				await redirect(redirectTo);
			}
		} catch (e: ApiError) {
			error = true;
			errorMessage = e.message;
		}
	}
</script>

<style lang="scss">
	form {
		max-width: 330px;
		margin: auto;
	}
</style>

<form on:submit|preventDefault={submit}>
    <h4>Inscription</h4>
    <div class="mb-3">
        <label class="form-label">Adresse email</label>
        <input type="email" name="email" class="form-control" placeholder="example@example.com" bind:value={email}>
    </div>

    <div class="mb-3">
        <label class="form-label">Mot de passe</label>
        <input type="password" name="password" class="form-control" placeholder="Mot de passe" bind:value={password}>
    </div>

    <div class="mb-3">
        <label class="form-label">Confirmer le mot de passe</label>
        <input type="password" name="password" class="form-control" placeholder="Mot de passe"
               bind:value={confirmPassword}>
    </div>

    <small class="form-check mb-3">
        <input class="form-check-input" type="checkbox" value="" id="tos" bind:checked={tos}>
        <label class="form-check-label" for="tos">
            J'ai pris connaissance et j'accepte les
            <a href="https://www.minecraft.net/fr-fr/eula" target="_blank" rel="noreferrer noopener">conditions
                générales d'utilisations</a>
            du site.
        </label>
    </small>

    <div class:visually-hidden={!error} class="text-danger mb-3">
        Erreur : {errorMessage}
    </div>

    <Button className="btn btn-primary" onClick={submit}>
        S'inscrire
    </Button>
</form>