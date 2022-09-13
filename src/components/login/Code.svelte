<script lang="ts">
	import type { ApiError } from "$shared/models";
	import { t } from "svelte-intl-precompile";
	import { Variant } from "$shared/constants.js";
	import { sendVerificationCode } from "./helpers";
	import Button from "$shared/Button.svelte";

	export let code;
	export let email;

	let codeError: ApiError | null = null;
	let codeSend = false;

	async function getCode() {
		const { error, response } = await sendVerificationCode(email);
		codeSend = !!response;
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
		/>
		<Button variant={Variant.SECONDARY} onClick={getCode}>{$t("get_code")}</Button>
	</div>
</div>

<div class:visually-hidden={!codeSend} class="text-muted mb-3">
	{$t("code_sent_check_mailbox")}
</div>
