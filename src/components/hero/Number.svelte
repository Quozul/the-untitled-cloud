<script lang="ts">
	export let number: string;
	export let text: string;
</script>

<style lang="scss" global>
	@include media-breakpoint-up(lg) {
		.number {
			flex: 1 1 auto;

			& + .number {
				border-left: solid 1px black;
			}
		}
	}
</style>

<div class="d-flex gap-2 number justify-content-center align-items-center">
	<div class="fw-bold fs-3">
		{number}
	</div>
	<div class="text-uppercase fs-4">
		{text}
	</div>
</div>