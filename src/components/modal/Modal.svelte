<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { Variant } from "$shared/constants";
	import Button from "$shared/Button.svelte";
	import { clickOutside } from "$shared/clickOutside";
	import { t } from "svelte-intl-precompile";

	export let visible = false;
	export let icon = "";
	export let title = "Confirmation";
	export let closeText = $t("close");
	export let okText: string = null;
	export let onClick: VoidFunction = null;
	export let variant: Variant = Variant.DARK;
	export let disabled = false;

	function hide() {
		visible = false;
	}
</script>

{#if visible}
	<div class="modal show d-block" tabindex="-1">
		<div class="backdrop bg-dark" class:d-none={!visible} class:d-block={visible} />

		<div class="modal-dialog" use:clickOutside on:click_outside={hide}>
			<div class="modal-content">
				<div class="modal-header">
					{#if icon}
						<div class="d-flex align-items-center gap-2">
							<Icon key={icon} />
							<h5 class="modal-title">{title}</h5>
						</div>
					{:else}
						<h5 class="modal-title">{title}</h5>
					{/if}

					<button type="button" class="btn-close" on:click|preventDefault={hide} />
				</div>

				<div class="modal-body p-0">
					<slot />
				</div>

				<div class="modal-footer">
					{#if closeText !== null}
						<Button variant={Variant.DARK} outline={true} onClick={hide}>{closeText}</Button
						>
					{/if}

					{#if okText !== null}
						<Button {variant} {onClick} {disabled}>{okText}</Button>
					{/if}
				</div>
			</div>
		</div>
	</div>
{/if}

<style>
	.backdrop {
		position: absolute;
		top: 0;
		left: 0;
		opacity: 0.2;
		overflow: hidden;
		width: 100vw;
		height: 100vh;
	}
</style>
