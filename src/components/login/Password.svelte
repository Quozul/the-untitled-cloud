<script lang="ts">
	import type { ApiError } from "../../shared/models";
	import { redirect } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { credentials, loginMode, token } from "../../store/store";
	import { LoginMode } from "./models/LoginMode";
	import { changePassword, sendVerificationCode } from "./helpers";

	// Props
	export let redirectTo: string;

	// Input fields
	let email: string | null = $credentials?.email ?? null;
	let password: string | null = null;
	let confirmPassword: string | null = null;
	let code: string | null = null;

	// States
	let error: AuthenticationErrors | null = null;
	let errorMessage: string | null = null;
	let codeSend: boolean = false;

	async function submit() {
		if (password !== confirmPassword) {
			error = AuthenticationErrors.PASSWORDS_DIFFER;
			return;
		}

		try {
			error = null;
			const res = await changePassword(email, password, code);
			$token = res.token;
			await redirect(redirectTo);
		} catch (e: ApiError) {
			error = e.code;
			errorMessage = e.message;
		}
	}

	async function getCode() {
		try {
			codeSend = false;
			error = null;
			await sendVerificationCode(email);
			codeSend = true;
		} catch (e: ApiError) {
			error = e.code;
			errorMessage = e.message;
		}
    }
</script>

<form>
    <h4>Changement de mot de passe</h4>

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
        <input type="password" name="password" class="form-control" placeholder="Confirmation" bind:value={confirmPassword}>
    </div>

    <div class="mb-3">
        <label class="form-label">Code de vérification</label>
        <div class="input-group mb-3">
            <input type="text" name="code" class="form-control" placeholder="123456" bind:value={code} maxlength="6">
            <Button className="btn btn-outline-secondary" onClick={getCode}>Obtenir le code</Button>
        </div>
    </div>

    <div class:visually-hidden={!codeSend} class="text-muted mb-3">
        Code envoyé, vérifiez votre boite mail.
    </div>

    <div class:visually-hidden={!error} class="text-danger mb-3">
        Erreur : {errorMessage}
    </div>

    <Button type="submit" className="btn btn-primary" onClick={submit}>
        Changer le mot de passe
    </Button>
</form>