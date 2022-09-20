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

	let password = "";
	let apiError: ApiError | null = null;
	let email: string;
	let ftpPassword = "";

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
		navigator.clipboard.writeText(import.meta.env.VITE_SERVER_ADDRESS);
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

		password = "";
		apiError = error;
	}
</script>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>{$t("files.ftp.ftp")}</h4>

	<p class="text-muted">
		{$t("files.ftp.instruction")}
	</p>

	<dl class="d-flex flex-column m-0">
		<div class="separation">
			<dt>{$t("files.ftp.address")}</dt>
			<dd>
				<div class="user-select-all d-inline">{import.meta.env.VITE_SERVER_ADDRESS}</div>

				<Tooltip icon="clipboard" onClick={copyAddress}>
					{$t("files.ftp.address_copied")}
				</Tooltip>
			</dd>
		</div>

		<div class="separation">
			{#if !$server}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12" />
				</p>
			{:else}
				<dt>{$t("files.ftp.username")}</dt>
				<dd>
					<div class="user-select-all d-inline">
						{$server.id}
					</div>

					<Tooltip icon="clipboard" onClick={copyId}>
						{$t("files.ftp.username_copied")}
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
				<dt>{$t("files.ftp.password")}</dt>
				<dd>
					{#if $server.hasFtpPassword}
						●●●●●●●●●●●●●●●●
						<Tooltip icon="question" bind:visible={tooltipVisible}>
							{$t("files.ftp.password_alert")}
							<br />
							<button
								type="button"
								on:click={showResetModal}
								class="d-block btn btn-sm btn-link p-0 text-start"
							>
								{$t("files.ftp.reset_password")}
							</button>
						</Tooltip>
					{:else}
						<Button onClick={showResetModal} variant={Variant.LINK} className="btn-sm">
							{$t("files.ftp.generate_password")}
						</Button>
					{/if}
				</dd>
			{/if}
		</div>
	</dl>
</div>

<Modal
	title={$t("files.reset_password.title")}
	bind:visible={resetPasswordModal}
	okText={$t("files.reset_password.ok")}
	onClick={handleSubmit}
	disabled={password.length === 0}
>
	<div class="p-3">
		{$t("files.reset_password.warn")}
	</div>

	<hr class="m-0" />

	<div class="p-3">
		<div class="mb-3">
			{$t("files.reset_password.instruction")}
		</div>

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

		{#if apiError}
			<Alert variant={Variant.DANGER}>
				{apiError.translatedMessage}
			</Alert>
		{/if}
	</div>
</Modal>

<Modal bind:visible={newPasswordModal} title={$t("files.new_password.title")}>
	<div class="p-3">
		<p>{$t("files.new_password.warn")}</p>

		<h6>{$t("files.new_password.generated_password")}</h6>

		<div class="d-flex align-items-center">
			<div class="user-select-all d-inline me-2">
				{ftpPassword}
			</div>

			<Tooltip icon="clipboard" onClick={copyPassword}>
				{$t("files.new_password.password_copied")}
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
