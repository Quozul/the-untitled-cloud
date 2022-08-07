<script lang="ts">
	import ServerInfo from "$components/app/tabs/info/ServerInfo.svelte";
	import ServerFtp from "$components/app/tabs/info/ServerFtp.svelte";
	import Parameters from "$components/app/tabs/info/Parameters.svelte";
	import Pending from "./tabs/info/errors/Pending.svelte";
	import NotFound from "./tabs/info/errors/NotFound.svelte";
	import { ServerSubscriptionStatus } from "./constants";
	import Suspended from "./tabs/info/errors/Suspended.svelte";
	import { server, fetchServerError } from "$store/store";
	import InternalError from "./tabs/info/errors/InternalError.svelte";
	import { refreshSelectedServer } from "./helpers";

	let isPending: boolean
	let isSuspended: boolean
	let containerNotFound: boolean

	$: {
		isPending = $server?.subscriptionStatus === ServerSubscriptionStatus.PENDING;
		isSuspended = $server?.subscriptionStatus === ServerSubscriptionStatus.SUSPENDED;
		containerNotFound = $server?.subscriptionStatus === ServerSubscriptionStatus.ACTIVE && !$server?.serverCreated;
	}
</script>

{#if isPending}
	<Pending/>
{:else if containerNotFound}
	<NotFound/>
{:else if isSuspended}
	<Suspended/>
{:else if $fetchServerError}
	<InternalError refresh={refreshSelectedServer}/>
{:else}
	<div class="d-flex gap-3 flex-column flex-xl-row">
		<ServerInfo/>
		<ServerFtp/>
	</div>

	{#if $server}
		<Parameters/>
	{/if}
{/if}