<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { Variant } from "$shared/constants";
	import Button from "../shared/Button.svelte";

	export let visible: boolean = false;
	export let icon: string = "";
	export let title: string = "Confirmation";
	export let closeText: string = "Fermer";
	export let okText: string = null;
	export let onClick: VoidFunction = null;
	export let variant: Variant = Variant.PRIMARY;

	function hide() {
		visible = false;
	}
</script>

<style>
    .backdrop {
        position: absolute;
        top: 0;
        left: 0;
        opacity: .2;
        overflow: hidden;
        width: 100vw;
        height: 100vh;
    }
</style>

<div class="modal" class:show={visible} class:d-block={visible} tabindex="-1">
	<div class="backdrop bg-dark" class:d-none={!visible} class:d-block={visible}></div>

	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				{#if icon}
					<div class="d-flex align-items-center gap-2">
						<Icon key={icon}/>
						<h5 class="modal-title">{title}</h5>
					</div>
				{:else}
					<h5 class="modal-title">{title}</h5>
				{/if}

				<button type="button" class="btn-close" on:click|preventDefault={hide}></button>
			</div>

			<div class="modal-body">
				<slot/>
			</div>

			<div class="modal-footer">
				{#if closeText !== null}
					<Button variant={Variant.SECONDARY} onClick={hide}>{closeText}</Button>
				{/if}

				{#if okText !== null}
					<Button {variant} onClick={onClick}>{okText}</Button>
				{/if}
			</div>
		</div>
	</div>
</div>