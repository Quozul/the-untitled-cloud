<script lang="ts">
	import type { SelectItem } from "./SelectItem";
	import { createEventDispatcher } from "svelte";

	const dispatch = createEventDispatcher();

	export let item: SelectItem;
	export let search: string;

	let hide: boolean;
	$: hide = !item.label.toLowerCase().includes(search.toLowerCase())

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
	}
</style>

<div class="option px-2 py-1" class:d-none={hide} on:click={select}>
	{item.label}
</div>
