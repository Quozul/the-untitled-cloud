<script lang="ts">
	export let key = "void";
	export let className = "";
	export let width = "16";
	export let height = "16";
	export let onClick: VoidFunction | null = null;
</script>

<svg class="icon {className}" {width} {height} on:click={onClick}>
	<use xlink:href="#{key}" />
</svg>

<style global>
	.icon {
		flex: none;
	}
</style>
