<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import type { ApiError } from "../shared/models";
	import { credentials, loginMode, token } from "../../store/store";
	import { redirect } from "../shared/helpers";
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
	<h4>{$t("login")}</h4>
	<div class="mb-3">
		<label for="email" class="form-label">{$t("email_address")}</label>
		<input id="email" type="email" name="email" class="form-control" placeholder={$t("email_address")} bind:value={email}>
	</div>

	<div>
		<label for="password" class="form-label">{$t("password")}</label>
		<input id="password" type="password" name="password" class="form-control" placeholder={$t("password")} bind:value={password}>
	</div>

	<button type="button" on:click={forgotPassword} class="d-block btn btn-sm btn-link p-0 mb-3">
		{$t("password_lost_question_mark")}
	</button>

	<div class:visually-hidden={!error} class="text-danger mb-3">
		Erreur : {errorMessage}
	</div>

	<Button type="submit" className="btn btn-primary" onClick={submit}>
		{$t("to_login")}
	</Button>
</form>