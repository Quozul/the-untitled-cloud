<script lang="ts">
	import { createEventDispatcher } from "svelte";

	// Constants
	const dispatch = createEventDispatcher();

	// Props
	export let name: string;
	export let disabled = false;
	export let id: string | null = null;
	export let opened = false;

	function toggleOpen() {
		opened = !opened;
		dispatch("click", { opened, id });
	}
</script>

<div class="accordion-item">
	<h2 class="accordion-header">
		<button
			class="accordion-button"
			class:text-muted={disabled}
			class:collapsed={!opened}
			type="button"
			on:click|preventDefault={toggleOpen}
			{disabled}
		>
			{name}
		</button>
	</h2>

	{#if opened}
		<div class="accordion-collapse collapse p-3" class:show={opened}>
			<slot />
		</div>
	{/if}
</div>
