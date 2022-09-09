<script lang="ts">
	import { Variant } from "./constants";
	import Icon from "$components/icons/Icon.svelte";

	export let variant: Variant = Variant.PRIMARY;
	export let icon: string | null = null;
	export let title: string = "";
	export let className: string = "";
</script>

<style lang="scss" global>
	.alert {
		.icon {
			width: 16px;
			height: 16px;
			flex: none;
		}
	}
</style>

<div class="alert alert-{variant} d-flex align-items-center gap-2 {className}">
	{#if icon && title}
		<h6 class="fw-bold d-flex align-items-center gap-2">
			<Icon key={icon}/>
			{title}
		</h6>
	{:else if icon}
		<Icon key={icon}/>

		<div>
			<slot/>
		</div>
	{:else}
		<slot/>
	{/if}
</div>