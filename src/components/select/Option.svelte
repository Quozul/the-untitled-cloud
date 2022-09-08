<script lang="ts">
	import type { SelectItem } from "./SelectItem";
	import { createEventDispatcher } from "svelte";

	const dispatch = createEventDispatcher();

	export let item: SelectItem;
	export let search: string;
	export let value: SelectItem | null = null;

	let hide: boolean, selected: boolean;
	$: hide = !item.label.toLowerCase().includes(search.toLowerCase());
	$: selected = item.value === value?.value;

	function select() {
		dispatch("click", item);
	}
</script>

<style lang="scss">
	.option {
		cursor: pointer;
		pointer-events: visible;
		user-select: none;

		&:hover {
			background-color: $light;
		}

		&.active {
			background-color: $primary;
			color: $white;
		}
	}
</style>

<div class="option px-2 py-1" class:active={selected} class:d-none={hide} on:click={select}>
	{item.label}
</div>
