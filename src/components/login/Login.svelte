<script lang="ts">
	import type { ApiError } from "../../shared/models";
	import { credentials, loginMode, token } from "../../store/store";
	import { redirect, signIn } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { LoginMode } from "./models/LoginMode";

	// Props
	export let redirectTo: string;

	let email = null, password = null;
	let error = false;
	let errorMessage = null;

	async function submit() {
		try {
			error = false;
			const res = await signIn(email, password);
			$token = res.token;
			await redirect(redirectTo);
		} catch (e: ApiError) {
			if (e.code === AuthenticationErrors.VERIFY_ACCOUNT) {
				$credentials = {email, password};
				$loginMode = LoginMode.VERIFICATION;
			} else {
				error = true;
				errorMessage = e.message;
			}
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
	<h4>Connexion</h4>
	<div class="mb-3">
		<label class="form-label">Adresse email</label>
		<input type="email" name="email" class="form-control" placeholder="example@example.com" bind:value={email}>
	</div>

	<div class="mb-3">
		<label class="form-label">Mot de passe</label>
		<input type="password" name="password" class="form-control" placeholder="Mot de passe" bind:value={password}>
	</div>

	<div class:visually-hidden={!error} class="text-danger mb-3">
		Erreur : {errorMessage}
	</div>

	<Button className="btn btn-primary" onClick={submit}>
		Se connecter
	</Button>
</form>