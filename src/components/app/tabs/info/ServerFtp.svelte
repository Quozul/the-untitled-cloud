<script lang="ts">
	import { server, token } from "$store/store";
	import Tooltip from "$components/tooltip/Tooltip.svelte";
	import Modal from "$components/modal/Modal.svelte";
	import { t } from "svelte-intl-precompile";
	import { onMount } from "svelte";
	import jwtDecode from "jwt-decode";
	import type { ApiError } from "$shared/models";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";
	import { resetFtpPassword } from "$components/app/helpers";
	import Alert from "$shared/Alert.svelte";

	let resetPasswordModal = false;
	let newPasswordModal = false;
	let tooltipVisible = false;

	let password: string = "";
	let apiError: ApiError | null = null;
	let email: string;
	let ftpPassword: string = "";

	onMount(() => {
		const jwt = jwtDecode($token);
		email = jwt.email;
	});

	function showResetModal() {
		tooltipVisible = false;
		resetPasswordModal = true;
	}

	function copyId() {
		navigator.clipboard.writeText($server.id);
	}

	function copyAddress() {
		navigator.clipboard.writeText("141.94.98.122");
	}

	function copyPassword() {
		navigator.clipboard.writeText(ftpPassword);
	}

	async function handleSubmit() {
		const { error, response } = await resetFtpPassword($server, password);

		if (response) {
			ftpPassword = response.ftpPassword;
			resetPasswordModal = false;
			newPasswordModal = true;
		}

		apiError = error;
	}
</script>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>FTP</h4>

	<p class="text-muted">
		Téléchargez un client FTP de votre choix puis utilisez les informations ci-dessous pour vous
		connecter.
	</p>

	<dl class="d-flex flex-column m-0">
		<div class="separation">
			<dt>Adresse du serveur FTP</dt>
			<dd>
				<div class="user-select-all d-inline">141.94.98.122</div>

				<Tooltip icon="clipboard" onClick={copyAddress}>
					Adresse copiée dans le presse-papier !
				</Tooltip>
			</dd>
		</div>

		<div class="separation">
			{#if !$server}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12" />
				</p>
			{:else}
				<dt>Identifiant</dt>
				<dd>
					<div class="user-select-all d-inline">
						{$server.id}
					</div>

					<Tooltip icon="clipboard" onClick={copyId}>
						Identifiant copié dans le presse-papier !
					</Tooltip>
				</dd>
			{/if}
		</div>

		<div class="separation">
			{#if !$server}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12" />
				</p>
			{:else}
				<dt>Mot de passe</dt>
				<dd>
					{#if $server.hasFtpPassword}
						●●●●●●●●●●●●●●●●
						<Tooltip icon="question" bind:visible={tooltipVisible}>
							Votre mot de passe est caché pour davantage de sécurité. En cas de perte
							du mot de passe, vous devrez le réinitialiser.<br />
							<button
								type="button"
								on:click={showResetModal}
								class="d-block btn btn-sm btn-link p-0 text-start"
							>
								Réinitialiser le mot de passe
							</button>
						</Tooltip>
					{:else}
						<Button onClick={showResetModal} variant={Variant.LINK} className="btn-sm">
							Générer un mot de passe
						</Button>
					{/if}
				</dd>
			{/if}
		</div>
	</dl>
</div>

<Modal
	title="Réinitialisation de mot de passe"
	bind:visible={resetPasswordModal}
	okText="Obtenir un nouveau mot de passe"
	onClick={handleSubmit}
	disabled={password.length === 0}
>
	<div class="p-3">
		Vous êtes sur le point de réinitialiser le mot de passe pour accéder au serveur FTP.
	</div>

	<hr class="m-0" />

	<div class="p-3">
		<div class="mb-3">
			Veuillez entrer le mot de passe de votre compte pour générer un nouveau mot de passe
			pour le serveur FTP.
		</div>

		<div class="mb-3">
			{$t("logged_in_as")}
			<div class="text-muted d-inline">
				{email}
			</div>
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

		{#if apiError}
			<Alert variant={Variant.DANGER}>
				{apiError.translatedMessage}
			</Alert>
		{/if}
	</div>
</Modal>

<Modal visible={newPasswordModal} title="Mot de passe">
	<div class="p-3">
		<p>Pensez à noter ou enregistrer le mot de passe.</p>

		<h6>Mot de passe généré :</h6>

		<div class="d-flex align-items-center">
			<div class="user-select-all d-inline me-2">
				{ftpPassword}
			</div>

			<Tooltip icon="clipboard" onClick={copyPassword}>
				Mot de passe copié dans le presse-papier !
			</Tooltip>
		</div>
	</div>
</Modal>

<style lang="scss">
	.element {
		flex: 1;
	}

	.separation {
		padding: 1em 0;
		display: flex;
		flex-direction: column;

		+ .separation {
			border-top: solid 1px black;
		}

		dd {
			margin: 0;
			flex-grow: 1;
		}
	}

	@include media-breakpoint-up(lg) {
		.separation {
			flex-direction: row;
		}

		dd {
			text-align: end;
		}
	}
</style>
