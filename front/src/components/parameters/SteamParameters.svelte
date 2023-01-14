<script lang="ts">
	import { Versions } from "$components/parameters/constants/versions";
	import Select from "$components/select/Select.svelte";
	import type { SelectItem } from "$components/select/SelectItem";
	import { parameters } from "$store/store";
	import { games } from "$components/parameters/constants/games";

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

<div class="bg-light p-4 d-flex element flex-column gap-3">
	<h4 class="mb-0">Paramètres du serveur Steam</h4>

	<div>
		<label class="form-label" for="game-search">Jeu</label>
		<Select
			items={games}
			{value}
			placeholder="Chercher un jeu..."
			on:select={handleSelect}
			id="game-search"
		/>
	</div>
</div>
