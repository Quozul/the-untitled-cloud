<script lang="ts">
	import Link from "$shared/Link.svelte";

	export let current = false;
	export let title: string;
	export let description: string;
	export let href: string;
</script>

<style lang="scss">
	.step {
		border-top: transparent 4px solid;
		cursor: pointer;
	}
</style>

<Link className="col text-decoration-none mx-2 text-black" {href}>
	<div class="step pt-3" class:border-primary={current} class:border-secondary={!current}>
		<h6 class="mb-0" class:text-primary={current} class:text-secondary={!current}>{title}</h6>
		<p>{description}</p>
	</div>
</Link>