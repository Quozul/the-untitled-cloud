<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import type { ApiError } from "$shared/models";
	import { redirect } from "$shared/helpers";
	import Button from "$shared/Button.svelte";
	import { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { credentials, loginMode } from "$store/store";
	import { LoginMode } from "./models/LoginMode";
	import { signUp } from "./helpers";
	import Link from "$shared/Link.svelte";

	// Props
	export let redirectTo: string;

	// Input fields
	let email = "";
	let password = "";
	let confirmPassword = null;
	let tos = false;

	// States
	let error: ApiError | null = null;

	async function submit() {
		if (password !== confirmPassword) {
			error = {
				code: AuthenticationErrors.PASSWORDS_DIFFER,
				translatedMessage: $t("error.password_differ"),
			};
			return;
		}

		try {
			error = null;
			const res = await signUp(email, password, "fr", tos);
			if (res.code === AuthenticationErrors.VERIFY_ACCOUNT) {
				$credentials = { email, password };
				$loginMode = LoginMode.VERIFICATION;
			} else {
				// This should never happen, but just in case...
				await redirect(redirectTo);
			}
		} catch (e: ApiError) {
			error = e;
		}
	}
</script>

<form>
	<h4>{$t("signup")}</h4>
	<div class="mb-3">
		<label for="email" class="form-label">{$t("email_address")}</label>
		<input
			id="email"
			type="email"
			name="email"
			class="form-control"
			placeholder={$t("email_address")}
			bind:value={email}
		/>
	</div>

	<div class="mb-3">
		<label for="password" class="form-label">{$t("password")}</label>
		<input
			id="password"
			type="password"
			name="password"
			class="form-control"
			placeholder={$t("password")}
			bind:value={password}
		/>
	</div>

	<div class="mb-3">
		<label for="confirm" class="form-label">{$t("confirm_password")}</label>
		<input
			id="confirm"
			type="password"
			name="password"
			class="form-control"
			placeholder={$t("confirm")}
			bind:value={confirmPassword}
		/>
	</div>

	<small class="form-check mb-3">
		<input id="tos" class="form-check-input" type="checkbox" value="" bind:checked={tos} />
		<label for="tos" class="form-check-label">
			{$t("i_acknowledge_and_accept")}
			<Link href="/tos">{$t("terms_of_service")}</Link>.
		</label>
	</small>

	<div class:visually-hidden={!error} class="text-danger mb-3">
		{error?.translatedMessage}
	</div>

	<Button type="submit" className="btn btn-primary" onClick={submit}>
		{$t("to_signup")}
	</Button>
</form>
