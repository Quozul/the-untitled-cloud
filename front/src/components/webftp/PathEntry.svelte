<script lang="ts">
	import { createEventDispatcher } from "svelte";

	export let file: string;
	export let index: number;
	export let last: boolean;

	const dispatch = createEventDispatcher();

	function handleClick() {
		dispatch("click", index);
	}
</script>

<li class="breadcrumb-item user-select-none text-truncate" class:cursor-default={last} class:cursor-pointer={!last} class:active={last} on:click|preventDefault={handleClick}>
	{file}
</li>