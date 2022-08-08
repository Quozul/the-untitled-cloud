<script lang="ts">
	import { onDestroy, onMount, setContext } from "svelte";
	import { type Unsubscriber, type Writable, writable } from "svelte/store";
	import { searchKey, valueKey, focusKey } from "./constants";
	import Icon from "$components/icons/Icon.svelte";
	import {clickOutside} from "$shared/clickOutside";
	import Option from "./Option.svelte";

	// Props
	export let defaultText: string = "";
	export let value: string = "";
	export let placeholder: string = "";

	// State
	let unsubscribeValue: Unsubscriber;

	// Contexts
	let selectedStore: Writable<string> = writable(defaultText);
	setContext(searchKey, selectedStore);

	let valueStore: Writable<string> = writable(value);
	setContext(valueKey, valueStore);

	let focus: Writable<boolean> = writable(false);
	setContext(focusKey, focus);

	$: $selectedStore = defaultText;

	onMount(() => {
		unsubscribeValue = valueStore.subscribe(v => {
			value = v;
		});
	});

	onDestroy(() => {
		unsubscribeValue?.();
	});

	let select: HTMLElement;
	let input: HTMLElement;

	function show() {
		$focus = true;
	}

	function hide() {
		$focus = false;
	}

	function clear() {
		$selectedStore = "";
		input.focus();
	}
</script>

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

<div class="select" on:focusin={show}  use:clickOutside on:click_outside={hide}>
	<input type="text" class="input form-control" bind:value={$selectedStore} bind:this={input} placeholder={placeholder}>

	{#if $selectedStore.length > 0}
		<Icon key="x-lg" className="select-icon cross" onClick={clear} />
	{:else}
		<Icon key="chevron-down" className="select-icon chevron" />
	{/if}

	<div class="options border bg-white w-100" class:d-none={!$focus} class:d-block={$focus}>
		<slot/>

		{#if $selectedStore.length > 0}
			<Option value={$selectedStore} text={$selectedStore} force={true}/>
		{/if}
	</div>
</div>