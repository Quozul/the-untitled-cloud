<script lang="ts">
	import type { ApiError } from "../../shared/models";
	import { credentials, loginMode, token } from "../../store/store";
	import { redirect } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { LoginMode } from "./models/LoginMode";
	import { signIn } from "./helpers";

	// Props
	export let redirectTo: string;

	// Input fields
	let email = null;
	let password = null;

	// States
	let error: AuthenticationErrors | null = null;
	let errorMessage: string | null = null;

	async function submit() {
		try {
			error = null;
			const res = await signIn(email, password);
			$token = res.token;
			await redirect(redirectTo);
		} catch (e: ApiError) {
			if (e.code === AuthenticationErrors.VERIFY_ACCOUNT) {
				$credentials = {email, password};
				$loginMode = LoginMode.VERIFICATION;
			} else {
				error = e.code;
				errorMessage = e.message;
			}
		}
	}

	async function forgotPassword() {
		$credentials = {
			email,
			password: null,
		};

		$loginMode = LoginMode.CHANGE_PASSWORD;
	}
</script>

<form>
	<h4>Connexion</h4>
	<div class="mb-3">
		<label class="form-label">Adresse email</label>
		<input type="email" name="email" class="form-control" placeholder="example@example.com" bind:value={email}>
	</div>

	<div>
		<label class="form-label">Mot de passe</label>
		<input type="password" name="password" class="form-control" placeholder="Mot de passe" bind:value={password}>
	</div>

	<button type="button" on:click={forgotPassword} class="d-block btn btn-sm btn-link p-0 mb-3">
		Mot de passer perdu ?
	</button>

	<div class:visually-hidden={!error} class="text-danger mb-3">
		Erreur : {errorMessage}
	</div>

	<Button type="submit" className="btn btn-primary" onClick={submit}>
		Se connecter
	</Button>
</form>