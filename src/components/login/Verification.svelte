<script lang="ts">
    import { t } from "svelte-intl-precompile";
	import type { ApiError } from "../shared/models";
	import { credentials, loginMode, token } from "../../store/store";
	import { redirect } from "../shared/helpers";
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
            const {email} = $credentials;
			const res = await sendVerificationCode(email);
			error = res.code;
			errorMessage = $t("code_sent_check_mailbox");
		} catch (e: ApiError) {
			error = e.code;
			errorMessage = e.message;
		}
	}
</script>

<form autocomplete="off">
    <h4>{$t("verification")}</h4>

    <p class="text-muted">
        {$t("please_enter_verification_code")}
    </p>

    <div class="mb-3">
        <label class="form-label">{$t("verification_code")}</label>
        <input type="text" name="code" class="form-control" placeholder="123456" bind:value={code} maxlength="6">
    </div>

    <div class:visually-hidden={!error} class="text-danger mb-3">
        {errorMessage}
    </div>

    <Button type="submit" onClick={submit}>
        {$t("to_login")}
    </Button>

    {#if error === AuthenticationErrors.EXPIRED_CODE || error === AuthenticationErrors.INVALID_CODE}
        <Button className="btn btn-secondary" onClick={resendCode}>
            {$t("resend_code")}
        </Button>
    {/if}
</form>