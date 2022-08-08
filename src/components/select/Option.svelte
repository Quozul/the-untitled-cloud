<script lang="ts">
	import { getContext } from "svelte";
	import { focusKey, searchKey, valueKey } from "./constants";
	import type { Writable } from "svelte/store";

	export let value: string = "";
	export let text: string = "";
	export let force: boolean = false;

	let search: Writable<string> = getContext(searchKey);
	let selectValue: Writable<string> = getContext(valueKey);
	let focus: Writable<boolean> = getContext(focusKey);

	let hide: boolean;
	$: hide = !force && text.toLowerCase() === $search.toLowerCase() || !text.toLowerCase().includes($search.toLowerCase())

	function select() {
		$selectValue = value;
		$search = text;
		$focus = false;
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
	{text}
</div>
