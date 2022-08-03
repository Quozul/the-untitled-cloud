<script lang="ts">
	import type { ApiError } from "../../shared/models";
	import { credentials, loginMode, token } from "../../store/store";
	import { redirect, signIn, signUp } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";
	import { LoginMode } from "./models/LoginMode";

	// Props
	export let redirectTo: string;

	let code = null;
	let error = false;
	let errorMessage = null;

	async function submit() {
		// In case the user has not logged before arriving there.
		if (!$credentials) {
			$loginMode = LoginMode.LOGIN;
			return;
		}

		try {
			error = false;
			const {email, password} = $credentials;
			const res = await signIn(email, password, code);
			$token = res.token;
			await redirect(redirectTo);
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
    <h4>Vérification</h4>
    <p class="text-muted">
        Veuillez entrer dans le champ ci-dessous le code de vérification qui vous a été envoyé par mail.
    </p>
    <div class="mb-3">
        <label class="form-label">Code de vérification</label>
        <input type="text" name="code" class="form-control" placeholder="123456" bind:value={code}>
    </div>

    <div class:visually-hidden={!error} class="text-danger mb-3">
        Erreur : {errorMessage}
    </div>

    <Button className="btn btn-primary" onClick={submit}>
        Se connecter
    </Button>
</form>