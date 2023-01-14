<script lang="ts">
	import { Variant } from "$shared/constants";
	import Button from "$shared/Button.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import Code from "$components/login/Code.svelte";
	import { token } from "$store/store";
	import { deleteAccount } from "./helpers";
	import { locale, t } from "svelte-intl-precompile";
	import type { ApiError } from "shared/models";
	import Alert from "$shared/Alert.svelte";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import jwtDecode from "jwt-decode";

	let modalVisible = false;
	let code: string | null = null;
	let password: string | null = null;
	let error: ApiError | null = null;
	let email: string;

	onMount(() => {
		const jwt = jwtDecode($token);
		email = jwt.email;
	});

	function openModal() {
		modalVisible = true;
	}

	async function handleSubmit() {
		const { error } = await deleteAccount(password, code);

		if (!error) {
			$token = null;
			await goto(`/${$locale}`);
		}
	}
</script>

<Modal
	bind:visible={modalVisible}
	onClick={handleSubmit}
	title={$t("authentication.deleting_account")}
	icon="trash"
	okText={$t("authentication.delete_account")}
	closeText={$t("common.cancel")}
	variant={Variant.DANGER}
>
	<div class="p-3">
		<p>{$t("authentication.delete_account_warn")}</p>
		<p class="m-0">{$t("authentication.all_subs_must_be_cancelled")}</p>
	</div>

	<hr class="m-0" />

	<div class="p-3">
		<div class="mb-3">
			{$t("authentication.logged_in_as")}
			<div class="text-muted d-inline">
				{email}
			</div>
		</div>

		<div class="mb-3">
			<label for="password" class="form-label">{$t("authentication.password")}</label>
			<input
				id="password"
				type="password"
				name="password"
				class="form-control"
				placeholder={$t("authentication.password")}
				bind:value={password}
			/>
		</div>

		<Code {email} bind:code />

		{#if error}
			<Alert variant={Variant.DANGER} icon="warning">
				{error.translatedMessage || error.message}
			</Alert>
		{/if}
	</div>
</Modal>

<Button variant={Variant.DANGER} onClick={openModal}>{$t("authentication.delete_account")}</Button>
