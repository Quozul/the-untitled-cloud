<script lang="ts">
	export let name: string;

	let isOpened: boolean = false;

	function toggleOpen() {
		isOpened = !isOpened;
	}
</script>

<div class="accordion-item">
	<h2 class="accordion-header">
		<button
			class="accordion-button"
			class:collapsed={!isOpened}
			type="button"
			on:click|preventDefault={toggleOpen}
		>
			{name}
		</button>
	</h2>
	<div class="accordion-collapse collapse p-3" class:show={isOpened}>
		<slot />
	</div>
</div>
