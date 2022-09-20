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
	title="Suppression du compte"
	icon="trash"
	okText="Supprimer mon compte"
	closeText="Annuler"
	variant={Variant.DANGER}
>
	<div class="p-3">
		<p>
			Vous êtes sur le point de de supprimer votre compte. Cette action est réalisée dès
			l'envoi de ce formulaire et est irréversible.
		</p>
		<p class="m-0">Tous vos abonnements doivent annulés pour procéder.</p>
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

<Button variant={Variant.DANGER} onClick={openModal}>Supprimer mon compte</Button>
