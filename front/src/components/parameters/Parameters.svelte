<script lang="ts">
	import MinecraftParameters from "$components/parameters/MinecraftParameters.svelte";
	import { server } from "$store/store";
	import { Products } from "$components/cart/constants";
	import NoParameters from "$components/errors/NoParameters.svelte";
	import SteamParameters from "$components/parameters/SteamParameters.svelte";
</script>

{#if $server?.product.id === Products.MinecraftServer}
	<MinecraftParameters />
{:else if $server?.product.id === Products.ArkServer}
	<SteamParameters />
{:else}
	<NoParameters />
{/if}
