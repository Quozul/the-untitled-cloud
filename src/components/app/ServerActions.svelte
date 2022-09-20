<script lang="ts">
	import { server, fetchingServer } from "$store/store";
	import "@js-joda/timezone";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "$components/app/helpers";
	import { Variant } from "$shared/constants";
	import Modal from "$components/modal/Modal.svelte";
	import { t } from "svelte-intl-precompile";
	import type { ApiError } from "$shared/models";
	import Alert from "$shared/Alert.svelte";

	let modalVisible = false;
	let apiError: ApiError | null = null;

	async function startServer() {
		await patchServer($server, "START");
		await refreshSelectedServer();
	}

	async function stopServer() {
		await patchServer($server, "STOP");
		await refreshSelectedServer();
	}

	async function restartServer() {
		await patchServer($server, "RESTART");
		await refreshSelectedServer();
	}

	function openModal() {
		modalVisible = true;
	}

	async function reset() {
		const { error, response } = await patchServer($server, "RESET");
		if (response) {
			await refreshSelectedServer();
			modalVisible = false;
		}

		apiError = error;
	}
</script>

<h4>{$t("server_info.actions")}</h4>
{#if $server.state.created}
	<div class="d-flex gap-3 flex-wrap">
		{#if !$server.state.running}
			<Button
				loading={$fetchingServer}
				disabled={!$server}
				icon="play"
				onClick={startServer}
			>
				{$t("action.start")}
			</Button>
		{:else}
			<Button
				loading={$fetchingServer}
				disabled={!$server}
				icon="stop"
				onClick={stopServer}
			>
				{$t("action.stop")}
			</Button>
		{/if}

		<Button
			loading={$fetchingServer}
			disabled={!$server}
			icon="arrow-clockwise"
			onClick={restartServer}
		>
			{$t("action.restart")}
		</Button>

		<Button onClick={refreshSelectedServer}>{$t("action.refresh")}</Button>

		<Button
			loading={$fetchingServer}
			disabled={!$server}
			icon="trash"
			onClick={openModal}
		>
			{$t("action.reset")}
		</Button>

		<Modal
			bind:visible={modalVisible}
			icon="trash"
			onClick={reset}
			title={$t("action.to_reset")}
			okText={$t("action.reset")}
			closeText={$t("common.cancel")}
			variant={Variant.DANGER}
		>
			<div class="p-3">
				<p>{$t("action.reset.warn")}</p>
				<p>{$t("action.proceed_question")}</p>

				{#if apiError}
					<Alert variant={Variant.DANGER} icon="warning" className="mt-3">
						{apiError.translatedMessage}
					</Alert>
				{/if}
			</div>
		</Modal>
	</div>
{/if}