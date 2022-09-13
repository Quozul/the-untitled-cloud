<script lang="ts">
	import { Versions } from "$components/parameters/constants/versions";
	import Select from "$components/select/Select.svelte";
	import type { SelectItem } from "$components/select/SelectItem";
	import { parameters } from "$store/store";

	const items: SelectItem[] = [
		{ label: "Dernière", value: "latest" },
		{ label: "Snapshot", value: "snapshot" },
	];

	for (const version of Versions) {
		items.push({ label: version, value: version });
	}

	let value: SelectItem = {
		value: $parameters.version,
		label: $parameters.version,
	};

	function handleSelect(event) {
		value = event.detail;
		$parameters.version = value.value;
	}
</script>

<div>
	<label class="form-label">Version de Minecraft</label>
	<Select
		{items}
		{value}
		placeholder="Chercher une version..."
		allowCustom={true}
		on:select={handleSelect}
	/>
</div>
