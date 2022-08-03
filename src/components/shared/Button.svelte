<script lang="ts">
    export let className: string = "btn btn-primary";
	export let disabled: boolean = false;
	export let type: string = "button";
	export let onClick: VoidFunction;

	let processing = false;

	async function handleClick() {
		try {
			processing = true;
			await onClick();
		} finally {
			processing = false;
		}
	}
</script>

<button {type} class="{className}" disabled="{disabled || processing}" on:click|preventDefault={handleClick}>
    {#if processing}
        <span class="spinner-border spinner-border-sm"></span>
    {/if}
    <slot/>
</button>