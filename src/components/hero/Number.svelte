<script lang="ts">
	export let number: string;
	export let text: string;
</script>

<div class="col-12 col-lg-4 d-flex gap-2 number align-items-center">
	<div class="fw-bold fs-3">
		{number}
	</div>
	<div class="text-uppercase fs-4">
		{text}
	</div>
</div>

<style lang="scss" global>
	@include media-breakpoint-up(lg) {
		.number {
			flex: 1 1 auto;
			justify-content: center;

			& + .number {
				border-left: solid 1px currentColor;
			}

			&:first-of-type {
				justify-content: flex-start;
			}

			&:last-of-type {
				justify-content: flex-end;
			}
		}
	}
</style>
