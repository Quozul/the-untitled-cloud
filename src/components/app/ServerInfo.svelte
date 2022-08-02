<script lang="ts">
	import { selectedServer } from "../../store/store";
	import Icon from "../../components/icons/Icon.svelte";
	import type { DetailedServer, Server } from "./models";
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
	let debug: boolean = false;
	let error: string | null = null;
	let fetching: boolean = false;

	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	async function fetchInfo(value: Server = $selectedServer) {
		if (!value) return;
		try {
			fetching = true;
			server = await getServerInfo(value.id);

			// Parse dates
			started = ZonedDateTime.parse(server.state.startedAt);
			stopped = ZonedDateTime.parse(server.state.finishedAt);
			if (stopped.year() === 1) {
				stopped = ZonedDateTime.now();
			}
			duration = Duration.between(started, stopped);

			error = null;
        } catch (err) {
            server = null;
			error = err;
		} finally {
			fetching = false;
		}
	}

	onMount(() => {
		unsubscribe = selectedServer.subscribe(fetchInfo);
	});

	onDestroy(() => {
		unsubscribe?.();
	});

	function startServer() {
		patchServer($selectedServer.id, "START");
	}

	function stopServer() {
		patchServer($selectedServer.id, "STOP");
	}

	function restartServer() {
		patchServer($selectedServer.id, "RESTART");
	}

	function toggleDebug() {
        debug = !debug;
	}
</script>

{#if !fetching && server}
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
{:else if fetching}
    <div class="d-flex align-items-center">
        <div class="spinner-border me-3" role="status" aria-hidden="true"></div>
        <strong>Chargement...</strong>
    </div>
{/if}

{#if error}
    <h2>Erreur</h2>
    {error}
{/if}

<hr>
<button type="button" class="btn btn-sm btn-light" on:click|preventDefault={toggleDebug}>Debug</button>
<pre class="collapse" class:show={debug}>{JSON.stringify(server, " ", 4)}</pre>