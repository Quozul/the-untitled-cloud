<script lang="ts">
    import { t } from "svelte-intl-precompile";
	import type { ApiError } from "$shared/models";
	import { redirect } from "$shared/helpers";
	import Button from "$shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { credentials, loginMode, token } from "$store/store";
	import { LoginMode } from "./models/LoginMode";
	import { changePassword, sendVerificationCode } from "./helpers";
    import { ButtonVariant } from "$shared/constants";
    import Icon from "$components/icons/Icon.svelte";

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

    function back() {
        $loginMode = LoginMode.LOGIN;
    }
</script>

<form>
    <h4 class="mb-0">{$t("password_change")}</h4>

    <button type="button" on:click={back} class="d-block btn btn-sm btn-link p-0 mb-3">
        <Icon key="chevron-left" height="12" width="12" className="me-1"/>
        {$t("back")}
    </button>

    <div class="mb-3">
        <label for="email" class="form-label">{$t("email")}</label>
        <input id="email" type="email" name="email" class="form-control" placeholder={$t("email")} bind:value={email}>
    </div>

    <div class="mb-3">
        <label for="password" class="form-label">{$t("password")}</label>
        <input id="password" type="password" name="password" class="form-control" placeholder={$t("password")} bind:value={password}>
    </div>

    <div class="mb-3">
        <label for="confirm" class="form-label">{$t("confirm_password")}</label>
        <input id="confirm" type="password" name="password" class="form-control" placeholder={$t("confirm")} bind:value={confirmPassword}>
    </div>

    <div class="mb-3">
        <label for="code" class="form-label">{$t("verification_code")}</label>
        <div class="input-group mb-3">
            <input id="code" type="text" name="code" class="form-control" placeholder="123456" bind:value={code} maxlength="6">
            <Button variant={ButtonVariant.SECONDARY} onClick={getCode}>{$t("get_code")}</Button>
        </div>
    </div>

    <div class:visually-hidden={!codeSend} class="text-muted mb-3">
        {$t("code_sent_check_mailbox")}
    </div>

    <div class:visually-hidden={!error} class="text-danger mb-3">
        Erreur : {errorMessage}
    </div>

    <Button type="submit" className="btn btn-primary" onClick={submit}>
        {$t("update_password")}
    </Button>
</form>