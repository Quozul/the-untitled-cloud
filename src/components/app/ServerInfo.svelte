<script lang="ts">
	import { fetchedServers, selectedServer } from "../../store/store";
	import Icon from "../../components/icons/Icon.svelte";
	import type { DetailedServer } from "./models";
	import { onDestroy, onMount } from "svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import "@js-joda/timezone";
	import { Locale } from "@js-joda/locale_fr";
	import { ServerStatus } from "./constants";
	import Parameters from "../../components/app/Parameters.svelte";
	import { getServerInfo, patchServer } from "../../shared/helpers";
	import type { Unsubscriber } from "svelte/store";

	let server: DetailedServer = null;
	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;
	let unsubscribe: Unsubscriber;

	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	async function fetchInfo(value = $selectedServer) {
		if (!$fetchedServers) return;
		server = await getServerInfo(value);

		started = ZonedDateTime.parse(server.state.startedAt);
		stopped = ZonedDateTime.parse(server.state.finishedAt);
		if (stopped.year() === 1) {
			stopped = ZonedDateTime.now();
		}
		duration = Duration.between(started, stopped);
	}

	onMount(() => {
		unsubscribe = selectedServer.subscribe(fetchInfo);
	});

	onDestroy(() => {
		unsubscribe?.();
	});

	function startServer() {
		patchServer($selectedServer, "START");
	}

	function stopServer() {
		patchServer($selectedServer, "STOP");
	}

	function restartServer() {
		patchServer($selectedServer, "RESTART");
	}
</script>

{#if server !== null}
    <h1
            class="d-flex align-items-center"
            class:text-success={server.state.status === ServerStatus.RUNNING}
            class:text-danger={server.state.status !== ServerStatus.RUNNING}
    >
        {#if server.state.status === ServerStatus.RUNNING}
            <Icon key="play" width="48" height="48"/>
        {:else}
            <Icon key="stop" width="48" height="48"/>
        {/if}
        {server.name}
    </h1>

    <dl>
        <dt>Dernier démarrage</dt>
        <dd>
            {#if server.state.status === ServerStatus.CREATED}
                Jamais démarré
            {:else}
                {ZonedDateTime.parse(server.state.startedAt).format(formatter)}
            {/if}
        </dd>

        <dt>État actuel</dt>
        <dd>{server.state.status}</dd>

        <dt>Adresse de connexion</dt>
        <dd>
            {#if !server.port}
                Démarrez le serveur pour avoir une adresse de connexion.
            {:else}
                localhost:{server.port}
            {/if}
        </dd>

        <dt>Accès aux fichiers du serveur</dt>
        <dd>
            Adresse du serveur : <pre class="d-inline">localhost:2121</pre>.
            <ol>
                <li>Téléchargez un client FTP de votre choix.</li>
                <li>Connectez-vous avec les mêmes identifiants que pour le site.</li>
                <li>Rendez-vous dans le dossier <pre class="d-inline">{server.id}</pre>.</li>
            </ol>
        </dd>
    </dl>

    <div class="row gx-3">
        <div class="col-auto">
            {#if !server.state.running}
                <button class="btn btn-primary d-flex align-items-center" on:click|preventDefault={startServer}>
                    <Icon key="play" className="me-2"/>
                    Démarrer
                </button>
            {:else}
                <button class="col btn btn-primary d-flex align-items-center" on:click|preventDefault={stopServer}>
                    <Icon key="stop" className="me-2"/>
                    Arrêter
                </button>
            {/if}
        </div>

        <div class="col-auto">
            <button class="col btn btn-primary d-flex align-items-center" on:click|preventDefault={restartServer}>
                <Icon key="arrow-clockwise" className="me-2"/>
                Redémarrer
            </button>
        </div>
    </div>

    <Parameters parameters={server.parameters}/>

    <h5>Debug</h5>
    <pre>{JSON.stringify(server, " ", 4)}</pre>
{:else}
    <div class="d-flex align-items-center">
        <div class="spinner-border me-3" role="status" aria-hidden="true"></div>
        <strong>Chargement...</strong>
    </div>
{/if}