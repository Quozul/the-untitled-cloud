<script lang="ts">
	import ServerInfo from "$components/app/tabs/info/ServerInfo.svelte";
	import Pending from "$components/errors/Pending.svelte";
	import NotFound from "$components/errors/NotFound.svelte";
	import Suspended from "$components/errors/Suspended.svelte";
	import { server, fetchServerError } from "$store/store";
	import InternalError from "$components/errors/InternalError.svelte";
	import { refreshSelectedServer } from "./helpers";
	import { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";

	let isPending: boolean;
	let isSuspended: boolean;
	let containerNotFound: boolean;

	$: {
		isPending =
			$server?.subscription.status === ApiSubscriptionStatus.PENDING ||
			$server?.state?.pending;
		isSuspended = $server?.subscription.status === ApiSubscriptionStatus.SUSPENDED;
		containerNotFound =
			$server?.subscription.status === ApiSubscriptionStatus.ACTIVE &&
			!$server?.state.created;
	}
</script>

{#if isPending}
	<Pending />
{:else if containerNotFound}
	<NotFound />
{:else if isSuspended}
	<Suspended />
{:else if $fetchServerError}
	<InternalError refresh={refreshSelectedServer} />
{:else}
	<ServerInfo />
{/if}
