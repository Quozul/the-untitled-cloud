<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";

	export let title: string;
	export let icon: string;
</script>

<div class="d-flex gap-3 align-items-center">
	<div class="rounded-circle bg-light d-flex align-items-center justify-content-center">
		<Icon key={icon} className="m-3" width="32" height="32" />
	</div>

	<div>
		<h5 class="text-uppercase fw-bold">{title}</h5>
		<slot />
	</div>
</div>