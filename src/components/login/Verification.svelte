<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import type { ApiError } from "$shared/models";
	import { credentials, loginMode, token } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { LoginMode } from "./models/LoginMode";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { sendVerificationCode, signIn } from "./helpers";
	import { createEventDispatcher } from "svelte";
	import { Variant } from "$shared/constants";

	// Constants
	const dispatch = createEventDispatcher();

	// Input fields
	let code = "";

	// States
	let verificationError: ApiError | null = null;

	$: if (code.length === 6) {
		submit();
	}

	async function submit() {
		// In case the user has not logged before arriving there.
		if (!$credentials) {
			$loginMode = LoginMode.LOGIN;
			return;
		}

		verificationError = null;
		const { email, password } = $credentials;
		const { error, response } = await signIn(email, password, code);

		if (response) {
			$token = response.token;
			dispatch("submit");
		}

		verificationError = error;
	}

	async function resendCode() {
		// In case the user has not logged before arriving there.
		if (!$credentials) {
			$loginMode = LoginMode.LOGIN;
			return;
		}

		const { email } = $credentials;
		const { error, response } = await sendVerificationCode(email);

		if (response) {
			verificationError = response;
		}

		verificationError = error;
	}
</script>

<form autocomplete="off">
	<p class="text-muted">
		{$t("authentication.please_enter_verification_code")}
	</p>

	<div class="mb-3">
		<label for="code" class="form-label">{$t("authentication.verification_code")}</label>
		<input
			id="code"
			type="text"
			name="code"
			autocomplete="one-time-code"
			class="form-control"
			placeholder="123456"
			bind:value={code}
			maxlength="6"
		/>
	</div>

	<div class:visually-hidden={!verificationError} class="text-danger mb-3">
		{verificationError?.translatedMessage}
	</div>

	<Button type="submit" onClick={submit} className="w-100 mb-3">
		{$t("authentication.to_login")}
	</Button>

	{#if verificationError && (verificationError.code === AuthenticationErrors.EXPIRED_CODE || verificationError.code === AuthenticationErrors.INVALID_CODE)}
		<Button variant={Variant.SECONDARY} onClick={resendCode} className="w-100">
			{$t("authentication.resend_code")}
		</Button>
	{/if}
</form>
