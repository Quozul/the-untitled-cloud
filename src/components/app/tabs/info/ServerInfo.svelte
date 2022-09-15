<script lang="ts">
	import { server, fetchingServer } from "$store/store";
	import { onMount } from "svelte";
	import { Duration, ZonedDateTime } from "@js-joda/core";
	import "@js-joda/timezone";
	import Button from "$shared/Button.svelte";
	import { patchServer, refreshSelectedServer } from "$components/app/helpers";
	import { Variant } from "$shared/constants";
	import Modal from "$components/modal/Modal.svelte";

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

	async function recreateServer() {
		await patchServer($server, "RECREATE");
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

<div class="bg-light p-4 d-flex flex-column element">
	<h4>Produit</h4>
	<dl class="d-flex flex-column m-0 mb-3">
		<div class="separation">
			<dt>Nom</dt>
			<dd>
				{$server.product.name}
			</dd>
		</div>

		<div class="separation">
			<dt>Description</dt>
			<dd>
				{$server.product.description}
			</dd>
		</div>
	</dl>

	<h4>Actions</h4>
	{#if $server?.state.created}
		<div class="d-flex gap-3 flex-wrap">
			{#if !$server.state.running}
				<Button
					variant={Variant.PRIMARY}
					loading={$fetchingServer}
					disabled={!$server}
					icon="play"
					onClick={startServer}
				>
					Démarrer
				</Button>
			{:else}
				<Button
					variant={Variant.PRIMARY}
					loading={$fetchingServer}
					disabled={!$server}
					icon="stop"
					onClick={stopServer}
				>
					Arrêter
				</Button>
			{/if}

			<Button
				variant={Variant.PRIMARY}
				loading={$fetchingServer}
				disabled={!$server}
				icon="arrow-clockwise"
				onClick={restartServer}
			>
				Redémarrer
			</Button>

			<Button onClick={refreshSelectedServer}>Rafraichir les informations</Button>

			<Button
				variant={Variant.PRIMARY}
				loading={$fetchingServer}
				disabled={!$server}
				icon="trash"
				onClick={openModal}
			>
				Réinitialiser
			</Button>

			<Button
				variant={Variant.PRIMARY}
				loading={$fetchingServer}
				disabled={!$server}
				icon="arrow-clockwise"
				onClick={recreateServer}
			>
				Recréer
			</Button>

			<Modal
				bind:visible={modalVisible}
				icon="trash"
				onClick={reset}
				title="Réinitialisation"
				okText="Réinitialiser"
				closeText="Annuler"
				variant={Variant.DANGER}
			>
				<div class="p-3">
					<p>
						Vous êtes sur le point de réinitialiser votre serveur. Cette action supprimera
						tous les fichiers et ne seront pas récupérables.
					</p>
					<p>Êtes-vous sûr de vouloir continuer ?</p>
				</div>
			</Modal>
		</div>
	{/if}
</div>

<style>
	.element {
		flex: 1;
	}
</style>
