<script lang="ts">
	import { selectedServer } from "$store/store";
	import type { DetailedServer } from "$components/app/models";
	import { onMount } from "svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import "@js-joda/timezone";
	import { Locale } from "@js-joda/locale_fr";
	import Button from "$shared/Button.svelte";
    import { patchServer, toggleRefreshServerInfo } from "$components/app/helpers";
    import { ButtonVariant } from "$shared/constants";

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
        if (server.state) {
            // Parse dates
            started = ZonedDateTime.parse(server.state.startedAt);
            stopped = ZonedDateTime.parse(server.state.finishedAt);
            if (stopped.year() === 1) {
                stopped = ZonedDateTime.now();
            }
            duration = Duration.between(started, stopped);
        }
	});

	async function startServer() {
		await patchServer($selectedServer.id, "START");
        toggleRefreshServerInfo();
	}

	async function stopServer() {
		await patchServer($selectedServer.id, "STOP");
        toggleRefreshServerInfo();
	}

	async function restartServer() {
		await patchServer($selectedServer.id, "RESTART");
        toggleRefreshServerInfo();
	}

	async function reset() {
		await patchServer($selectedServer.id, "RESET");
        toggleRefreshServerInfo();
	}
</script>

<style>
    .element {
        flex: 1;
    }
</style>

<div class="bg-light p-4 d-flex flex-column element">
    <h4>Actions</h4>

    {#if !fetching && server?.serverCreated}
        <div class="d-flex gap-3 flex-wrap">
            {#if !server.state.running}
                <Button variant={ButtonVariant.PRIMARY} loading={!server} icon="play" onClick={startServer}>
                    Démarrer
                </Button>
            {:else}
                <Button variant={ButtonVariant.PRIMARY} loading={!server} icon="stop" onClick={stopServer}>
                    Arrêter
                </Button>
            {/if}

            <Button variant={ButtonVariant.PRIMARY} loading={!server} icon="arrow-clockwise" onClick={restartServer}>
                Redémarrer
            </Button>

            <button class="btn btn-primary" on:click|preventDefault={toggleRefreshServerInfo}>Rafraichir les informations</button>

            <Button variant={ButtonVariant.PRIMARY} loading={!server} icon="trash" onClick={reset}>
                Réinitialiser
            </Button>
        </div>
    {:else if fetching}
        <div class="d-flex align-items-center">
            <div class="spinner-border me-3"></div>
            <strong>Chargement...</strong>
        </div>
    {/if}
</div>