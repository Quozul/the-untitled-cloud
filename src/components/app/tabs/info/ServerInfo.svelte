<script lang="ts">
	import { selectedServer, refreshServer } from "../../../../store/store";
	import Icon from "../../../icons/Icon.svelte";
	import type { DetailedServer } from "../../models";
	import { onMount } from "svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import "@js-joda/timezone";
	import { Locale } from "@js-joda/locale_fr";
	import { ServerStatus } from "../../constants";
	import Parameters from "./Parameters.svelte";
	import { patchServer } from "../../../../shared/helpers";
	import Button from "../../../shared/Button.svelte";
	import { refresh } from "../../helpers.js";

	export let server: DetailedServer;
	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let debug: boolean = false;
	let error: string | null = null;
	let fetching: boolean = false;

	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	onMount(() => {
		// Parse dates
		started = ZonedDateTime.parse(server.state.startedAt);
		stopped = ZonedDateTime.parse(server.state.finishedAt);
		if (stopped.year() === 1) {
			stopped = ZonedDateTime.now();
		}
		duration = Duration.between(started, stopped);
	});

	async function startServer() {
		await patchServer($selectedServer.id, "START");
		$refreshServer = true;
	}

	async function stopServer() {
		await patchServer($selectedServer.id, "STOP");
		$refreshServer = true;
	}

	async function restartServer() {
		await patchServer($selectedServer.id, "RESTART");
		$refreshServer = true;
	}

	async function recreate() {
		await patchServer($selectedServer.id, "RECREATE");
		$refreshServer = true;
	}

	async function reset() {
		await patchServer($selectedServer.id, "RESET");
		$refreshServer = true;
	}
</script>

<style>
    .element {
        flex: 1;
    }
</style>

<div class="bg-light p-4 d-flex flex-column element">
    <h4>Actions</h4>

    {#if !fetching && server}
        <div class="d-flex gap-3 flex-wrap">
            {#if !server.state.running}
                <Button className="btn btn-primary d-flex align-items-center" onClick={startServer}>
                    <Icon key="play" className="me-2"/>
                    Démarrer
                </Button>
            {:else}
                <Button className="btn btn-primary d-flex align-items-center" onClick={stopServer}>
                    <Icon key="stop" className="me-2"/>
                    Arrêter
                </Button>
            {/if}

            <Button className="btn btn-primary d-flex align-items-center" onClick={restartServer}>
                <Icon key="arrow-clockwise" className="me-2"/>
                Redémarrer
            </Button>

            <button class="btn btn-primary" on:click|preventDefault={refresh}>Rafraichir les informations</button>

            <Button className="btn btn-danger d-flex align-items-center" onClick={reset}>
                <Icon key="trash" className="me-2"/>
                Réinitialiser
            </Button>
        </div>
    {:else if fetching}
        <div class="d-flex align-items-center">
            <div class="spinner-border me-3" role="status" aria-hidden="true"></div>
            <strong>Chargement...</strong>
        </div>
    {/if}

    {#if error}
        <div class="p-5 d-flex justify-content-center align-items-center flex-column gap-3">
            <p class="text-center fs-4">
                Nous n'avons pas réussi à trouver votre serveur.
                <br/>
                Essayez de le recréer.
            </p>

            <Button className="btn btn-danger btn-lg" onClick={recreate}>
                Recréer
            </Button>
        </div>
    {/if}
</div>