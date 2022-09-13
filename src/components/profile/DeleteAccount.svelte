<script lang="ts">
	import { Variant } from "$shared/constants.js";
	import Button from "$shared/Button.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import Code from "$components/login/Code.svelte";
	import { token, user } from "$store/store.js";
	import { deleteAccount } from "./helpers";
	import { locale, t } from "svelte-intl-precompile";
	import type { ApiError } from "shared/models";
	import Alert from "$shared/Alert.svelte";
	import { goto } from "$app/navigation";

	let modalVisible: boolean = false;
	let code: string | null = null;
	let password: string | null = null;
	let error: ApiError | null = null;

	function openModal() {
		modalVisible = true;
	}

	async function handleSubmit() {
		try {
			await deleteAccount(password, code);
			$token = null;
			await goto(`/${$locale}`);
		} catch (e: ApiError) {
			error = e;
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
	<p>
		Vous êtes sur le point de de supprimer votre compte. Cette action est réalisée dès l'envoi
		de ce formulaire et est irréversible.
	</p>
	<p>Tous vos abonnements doivent annulés pour procéder.</p>

	<hr />

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

	<Code email={$user?.email} bind:code />

	{#if error}
		<Alert variant={Variant.DANGER} icon="warning">
			{error.translatedMessage || error.message}
		</Alert>
	{/if}
</Modal>

<Button variant={Variant.DANGER} onClick={openModal}>Supprimer mon compte</Button>
