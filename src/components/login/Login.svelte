<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import type { ApiError } from "$shared/models";
	import { credentials, loginMode, token } from "$store/store";
	import Button from "$shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { LoginMode } from "./models/LoginMode";
	import { signIn } from "./helpers";
	import { createEventDispatcher } from "svelte";
	import { Variant } from "$shared/constants";

	// Constants
	const dispatch = createEventDispatcher();

	// Input fields
	let email = null;
	let password = null;

	// States
	let loginError: ApiError | null = null;

	async function submit() {
		loginError = null;

		const { error, response } = await signIn(email, password);

		if (response) {
			$token = response.token;
			dispatch("submit");
		}

		if (error?.code === AuthenticationErrors.VERIFY_ACCOUNT) {
			$credentials = { email, password };
			$loginMode = LoginMode.VERIFICATION;
		}

		loginError = error;
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
	<div class="mb-3">
		<label for="email" class="form-label">{$t("email_address")}</label>
		<input
			id="email"
			type="email"
			name="email"
			autocomplete="email"
			class="form-control"
			placeholder={$t("email_address")}
			bind:value={email}
		/>
	</div>

	<div>
		<label for="password" class="form-label">{$t("password")}</label>
		<input
			id="password"
			type="password"
			name="password"
			autocomplete="current-password"
			class="form-control"
			placeholder={$t("password")}
			bind:value={password}
		/>
	</div>

	<button type="button" on:click={forgotPassword} class="d-block btn btn-sm btn-link p-0 mb-3">
		{$t("password_lost_question_mark")}
	</button>

	<div class:visually-hidden={!loginError} class="text-danger mb-3">
		{loginError?.translatedMessage}
	</div>

	<Button type="submit" variant={Variant.DARK} onClick={submit} className="w-100">
		{$t("to_login")}
	</Button>
</form>
