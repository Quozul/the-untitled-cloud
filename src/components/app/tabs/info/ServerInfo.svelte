<script lang="ts">
	import { server } from "$store/store";
	import "@js-joda/timezone";
	import ServerBarInfo from "$components/app/ServerBarInfo.svelte";
	import { t } from "svelte-intl-precompile";
	import ServerActions from "$components/app/ServerActions.svelte";
</script>

<div class="bg-light p-4 d-flex flex-column element gap-3">
	{#if $server.subscription.active}
		<div class="d-block d-xl-none">
			<h4>{$t("server_bar.state")}</h4>
			<ServerBarInfo />
		</div>
	{/if}

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

	{#if $server.subscription.active}
		<div>
			<ServerActions/>
		</div>
	{/if}
</div>

<style>
	.element {
		flex: 1;
	}
</style>
