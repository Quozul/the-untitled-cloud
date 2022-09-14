<script lang="ts">
	import type { ApiError } from "$shared/models";
	import { t } from "svelte-intl-precompile";
	import { Variant } from "$shared/constants.js";
	import { sendVerificationCode } from "./helpers";
	import Button from "$shared/Button.svelte";
	import Alert from "$shared/Alert.svelte";

	export let code;
	export let email;

	let codeError: ApiError | null = null;
	let codeSent = false;

	async function getCode() {
		const { error } = await sendVerificationCode(email);
		codeSent = true;
		codeError = error;
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
			disabled={!email}
			autocomplete="one-time-code"
		/>

		<Button variant={Variant.SECONDARY} onClick={getCode} disabled={!email}
			>{$t("get_code")}</Button
		>
	</div>
</div>

{#if codeError}
	<Alert variant={Variant.DANGER}>
		{codeError.translatedMessage}
	</Alert>
{:else if codeSent}
	<Alert variant={Variant.INFO}>
		{$t("code_sent_check_mailbox")}
	</Alert>
{/if}
