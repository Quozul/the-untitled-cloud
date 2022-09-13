<script lang="ts">
	import type { ApiError } from "$shared/models";
	import type { AuthenticationErrors } from "./models/AuthenticationErrors";
	import { t } from "svelte-intl-precompile";
	import { Variant } from "$shared/constants.js";
	import { sendVerificationCode } from "./helpers";
	import Button from "$shared/Button.svelte";

	export let code;
	export let email;

	let error: AuthenticationErrors | null = null;
	let errorMessage: string | null = null;
	let codeSend: boolean = false;

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

<div class="mb-3">
	<label for="code" class="form-label">{$t("verification_code")}</label>
	<div class="input-group mb-3">
		<input
			id="code"
			type="text"
			name="code"
			class="form-control"
			placeholder="123456"
			bind:value={code}
			maxlength="6"
		/>
		<Button variant={Variant.SECONDARY} onClick={getCode}>{$t("get_code")}</Button>
	</div>
</div>

<div class:visually-hidden={!codeSend} class="text-muted mb-3">
	{$t("code_sent_check_mailbox")}
</div>
