<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { clickOutside } from "$shared/clickOutside";
	import Option from "./Option.svelte";
	import type { SelectItem } from "./SelectItem";
	import { createEventDispatcher } from "svelte";

	const dispatch = createEventDispatcher();

	// Props
	export let placeholder: string = "Search...";
	export let items: SelectItem[];
	export let value: SelectItem;
	export let allowCustom: boolean = false;

	// State
	let search: string = value.label;
	let focus: boolean = false;

	let input: HTMLElement;

	$: if (!focus) {
		search = value.label;
	}

	function show() {
		search = "";
		focus = true;
	}

	function hide() {
		focus = false;
	}

	function clear() {
		search = "";
		input.focus();
	}

	function handleSelect(event) {
		hide();
		dispatch("select", event.detail);
	}

	function handleChange() {
		const searchValue = items.find((v) => v.label === search);

		if (searchValue) {
			hide();
			dispatch("select", searchValue);
		}
	}
</script>

<div class="select" on:focusin={show} use:clickOutside on:click_outside={hide}>
	<input
		type="text"
		class="input form-control"
		bind:value={search}
		bind:this={input}
		{placeholder}
		autocomplete="off"
		on:change={handleChange}
	/>

	{#if search.length > 0}
		<Icon key="x-lg" className="select-icon cross" onClick={clear} />
	{:else}
		<Icon key="chevron-down" className="select-icon chevron" />
	{/if}

	<div class="options border bg-white w-100" class:d-none={!focus} class:d-block={focus}>
		<slot />

		{#each items as item}
			<Option {item} {search} {value} on:click={handleSelect} />
		{/each}

		{#if allowCustom && search.length > 0}
			<Option item={{ label: search, value: search }} {search} on:click={handleSelect} />
		{/if}
	</div>
</div>

<style lang="scss">
	.select {
		position: relative;

		.input {
			cursor: default;
		}

		.options {
			position: absolute;
			top: 100%;
			z-index: 100;
			max-height: 250px;
			overflow-y: auto;
		}
	}

	:global {
		.select-icon {
			position: absolute;
			top: 50%;
			right: 0;
			transform: translate(-50%, -50%);

			&.chevron {
				pointer-events: none;
			}

			&.cross {
				z-index: 50;
				cursor: pointer;
			}
		}
	}
</style>
