<script lang="ts">
	import { Versions } from "$components/parameters/constants/versions";
	import Select from "$components/select/Select.svelte";
	import type { SelectItem } from "$components/select/SelectItem";
	import { parameters } from "$store/store";
	import { t } from "svelte-intl-precompile";

	const items: SelectItem[] = [
		{ label: $t("latest_version"), value: "latest" },
		{ label: $t("snapshot_version"), value: "snapshot" },
	];

	for (const version of Versions) {
		items.push({ label: version, value: version });
	}

	let value: SelectItem =
		{
			value: $parameters.version,
			label: $parameters.version,
		} || items[0];

	function handleSelect(event) {
		value = event.detail;
		$parameters.version = value.value;
	}
</script>

<div>
	<label class="form-label" for="minecraft-version">{$t("parameters.minecraft_version")}</label>

	<Select
		{items}
		{value}
		id="minecraft-version"
		placeholder={$t("parameters.search_version")}
		allowCustom={true}
		on:select={handleSelect}
	/>
</div>
