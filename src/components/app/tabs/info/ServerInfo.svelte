<script lang="ts">
    import { server, fetchingServer } from "$store/store";
	import { onMount } from "svelte";
	import { DateTimeFormatter, Duration, ZonedDateTime } from "@js-joda/core";
	import "@js-joda/timezone";
	import { Locale } from "@js-joda/locale_fr";
	import Button from "$shared/Button.svelte";
    import { patchServer, refreshSelectedServer } from "$components/app/helpers";
    import { ButtonVariant } from "$shared/constants";

	let started: ZonedDateTime = null;
	let stopped: ZonedDateTime = null;
	let duration: Duration = Duration.ZERO;

	const formatter = DateTimeFormatter
		.ofPattern("eeee d MMMM yyyy")
		.withLocale(Locale.FRANCE);

	onMount(() => {
        if ($server?.state) {
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
		await patchServer($server.id, "START");
        await refreshSelectedServer();
	}

	async function stopServer() {
		await patchServer($server.id, "STOP");
        await refreshSelectedServer();
	}

	async function restartServer() {
		await patchServer($server.id, "RESTART");
        await refreshSelectedServer();
	}

	async function reset() {
		await patchServer($server.id, "RESET");
        await refreshSelectedServer();
	}
</script>

<style>
    .element {
        flex: 1;
    }
</style>

<div class="bg-light p-4 d-flex flex-column element">
    <h4>Actions</h4>

    {#if $server?.serverCreated}
        <div class="d-flex gap-3 flex-wrap">
            {#if !$server.state.running}
                <Button variant={ButtonVariant.PRIMARY} loading={$fetchingServer} disabled="{!$server}" icon="play" onClick={startServer}>
                    Démarrer
                </Button>
            {:else}
                <Button variant={ButtonVariant.PRIMARY} loading={$fetchingServer} disabled="{!$server}" icon="stop" onClick={stopServer}>
                    Arrêter
                </Button>
            {/if}

            <Button variant={ButtonVariant.PRIMARY} loading={$fetchingServer} disabled="{!$server}" icon="arrow-clockwise" onClick={restartServer}>
                Redémarrer
            </Button>

            <button class="btn btn-primary" on:click|preventDefault={refreshSelectedServer}>Rafraichir les informations</button>

            <Button variant={ButtonVariant.PRIMARY} loading={$fetchingServer} disabled="{!$server}" icon="trash" onClick={reset}>
                Réinitialiser
            </Button>
        </div>
    {/if}
</div>