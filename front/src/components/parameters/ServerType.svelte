<script lang="ts">
	import { ServerType } from "$components/app/constants";
	import { parameters } from "$store/store";
	import { capitalize } from "$shared/helpers";
	import { t } from "svelte-intl-precompile";
	import Select from "$components/select/Select.svelte";
	import ServerVersion from "$components/parameters/ServerVersion.svelte";
	import type { SelectItem } from "$components/select/SelectItem";

	let defaultVersionName: string;
	$: if ($parameters.version === "latest" || $parameters.version === "snapshot") {
		defaultVersionName = $t(`${$parameters.version}_version`);
	} else {
		defaultVersionName = $parameters.version;
	}

	const items: SelectItem[] = [];
	let value: SelectItem;

	for (const type of Object.keys(ServerType)) {
		const item = { value: type, label: capitalize(type) };
		items.push(item);

		if (!value || type === $parameters.serverType) {
			value = item;
		}
	}

	function handleSelect(event) {
		value = event.detail;
		$parameters.serverType = value.value;
	}
</script>

<div class="d-flex flex-column gap-3 justify-content-start">
	<div class="d-flex flex-column flex-lg-row gap-3 justify-content-start">
		<div>
			<label class="form-label" for="server-type">{$t("parameters.server_type")}</label>
			<Select
				{items}
				{value}
				placeholder={$t("parameters.search_type")}
				on:select={handleSelect}
				id="server-type"
			/>
		</div>

		<ServerVersion />
	</div>

	<div class="d-flex flex-column flex-lg-row gap-3 justify-content-start">
		{#if $parameters.serverType === ServerType.FORGE}
			<div>
				<label for="forgeVersion" class="form-label">{$t("parameters.forge_version")}</label
				>
				<input
					id="forgeVersion"
					class="form-control"
					placeholder="41.1.0"
					bind:value={$parameters.forgeVersion}
				/>
			</div>
		{:else if $parameters.serverType === ServerType.FABRIC}
			<div>
				<label for="fabricLauncherVersion" class="form-label">
					{$t("parameters.fabric_launcher_version")}
				</label>
				<input
					id="fabricLauncherVersion"
					class="form-control"
					placeholder="0.11.0"
					bind:value={$parameters.fabricLauncherVersion}
				/>
			</div>

			<div>
				<label for="fabricLoaderVersion" class="form-label"
					>{$t("parameters.fabric_version")}</label
				>
				<input
					id="fabricLoaderVersion"
					class="form-control"
					placeholder="0.13.1"
					bind:value={$parameters.fabricLoaderVersion}
				/>
			</div>
		{:else if $parameters.serverType === ServerType.QUILT}
			<div>
				<label for="quiltLauncherVersion" class="form-label">
					{$t("parameters.quilt_launcher_version")}
				</label>
				<input
					id="quiltLauncherVersion"
					class="form-control"
					placeholder="0.16.0"
					bind:value={$parameters.quiltLauncherVersion}
				/>
			</div>

			<div>
				<label for="quiltLoaderVersion" class="form-label"
					>{$t("parameters.quilt_version")}</label
				>
				<input
					id="quiltLoaderVersion"
					class="form-control"
					placeholder="0.4.1"
					bind:value={$parameters.quiltLoaderVersion}
				/>
			</div>
		{:else if $parameters.serverType === ServerType.FTBA}
			<div>
				<label for="ftbModpackId" class="form-label"
					>{$t("parameters.ftb_modpack_id")}</label
				>
				<input
					id="ftbModpackId"
					class="form-control"
					placeholder="35"
					bind:value={$parameters.ftbModpackId}
				/>
			</div>

			<div>
				<label for="ftpModpackVersionId" class="form-label">
					{$t("parameters.ftb_modpack_version")}
				</label>
				<input
					id="ftpModpackVersionId"
					class="form-control"
					placeholder="2129"
					bind:value={$parameters.ftbModpackVersionId}
				/>
			</div>
		{/if}
	</div>
</div>
