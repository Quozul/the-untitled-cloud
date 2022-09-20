<script lang="ts">
	import { server, fetchingServer } from "$store/store";
	import { onMount } from "svelte";
	import { Duration, ZonedDateTime } from "@js-joda/core";
	import "@js-joda/timezone";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "$components/app/helpers";
	import { Variant } from "$shared/constants";
	import Modal from "$components/modal/Modal.svelte";
	import ServerBarInfo from "$components/app/ServerBarInfo.svelte";
	import { t } from "svelte-intl-precompile";

	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let modalVisible = false;

	onMount(() => {
		if ($server?.state && $server?.state?.created) {
			// Parse dates
			started = ZonedDateTime.parse($server.state.startedAt);
			stopped = ZonedDateTime.parse($server.state.finishedAt);
			if (stopped.year() === 1) {
				stopped = ZonedDateTime.now();
			}
			duration = Duration.between(started, stopped);
		}
	});

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
		try {
			await patchServer($server, "RESET");
			await refreshSelectedServer();
		} finally {
			modalVisible = false;
		}
	}
</script>

<div class="bg-light p-4 d-flex flex-column element gap-3">
	<div class="d-block d-xl-none">
		<h4>{$t("server_bar.state")}</h4>
		<ServerBarInfo />
	</div>

	<div>
		<h4>{$t("common.product")}</h4>
		<dl class="d-flex flex-column m-0 mb-3">
			<div class="separation">
				<dt>{$t("server_info.name")}</dt>
				<dd>
					{$server.product.name}
				</dd>
			</div>

			<div class="separation">
				<dt>{$t("server_info.description")}</dt>
				<dd>
					{$server.product.description}
				</dd>
			</div>
		</dl>
	</div>

	<div>
		<h4>{$t("server_info.actions")}</h4>
		{#if $server?.state.created}
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
					</div>
				</Modal>
			</div>
		{/if}
	</div>
</div>

<style>
	.element {
		flex: 1;
	}
</style>
