<script lang="ts">
	import type { ApiError } from "../../shared/models";
	import { credentials, loginMode, token } from "../../store/store";
	import { redirect } from "../../shared/helpers";
	import Button from "../shared/Button.svelte";
	import { LoginMode } from "./models/LoginMode";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { sendVerificationCode, signIn } from "./helpers";

	// Props
	export let redirectTo: string;

	// Input fields
	let code: string = "";

	// States
	let error: AuthenticationErrors | null = null;
	let errorMessage: string | null = null;

	$: if (code.length === 6) {
		submit();
    }

	async function submit() {
		// In case the user has not logged before arriving there.
		if (!$credentials) {
			$loginMode = LoginMode.LOGIN;
			return;
		}

		try {
			error = null;
			const {email, password} = $credentials;
			const res = await signIn(email, password, code);
			$token = res.token;
			await redirect(redirectTo);
		} catch (e: ApiError) {
			error = e.code;
			errorMessage = e.message;
		}
	}

	async function resendCode() {
		// In case the user has not logged before arriving there.
		if (!$credentials) {
			$loginMode = LoginMode.LOGIN;
			return;
		}

		try {
            const {email, password} = $credentials;
			const res = await sendVerificationCode(email, password);
			error = res.code;
			errorMessage = "Code envoyé, vérifiez votre boite mail.";
		} catch (e: ApiError) {
			error = e.code;
			errorMessage = e.message;
		}
	}
</script>

<form autocomplete="off">
    <h4>Vérification</h4>

    <p class="text-muted">
        Veuillez entrer dans le champ ci-dessous le code de vérification qui vous a été envoyé par mail.
    </p>

    <div class="mb-3">
        <label class="form-label">Code de vérification</label>
        <input type="text" name="code" class="form-control" placeholder="123456" bind:value={code} maxlength="6">
    </div>

    <div class:visually-hidden={!error} class="text-danger mb-3">
        {errorMessage}
    </div>

    <Button type="submit" onClick={submit}>
        Se connecter
    </Button>

    {#if error === AuthenticationErrors.EXPIRED_CODE || error === AuthenticationErrors.INVALID_CODE}
        <Button className="btn btn-secondary" onClick={resendCode}>
            Renvoyer un code
        </Button>
    {/if}
</form>