<script>
	import Button from "$shared/Button.svelte";
	import { toggleSidebarCollapsed } from "$components/sidebar/helpers";
	import { Variant } from "$shared/constants";
</script>

<div class="py-3 d-flex align-items-center gap-2 px-3">
	<Button
		icon="list"
		onClick={toggleSidebarCollapsed}
		variant={Variant.NONE}
		className="d-inline d-lg-none"
	/>

	<slot />
</div>

<hr class="d-block d-lg-none m-0" />

