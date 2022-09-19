<script lang="ts">
	import Icon from "$components/icons/Icon.svelte";
	import { createPopper } from "@popperjs/core";

	// Props
	export let icon: string;
	export let onClick: VoidFunction | null = null;

	// State
	let visible = false;
	let element: HTMLElement;
	let content: HTMLElement;

	$: if (element && content) {
		createPopper(element, content, {
			placement: "top",
			modifiers: [
				{
					name: "offset",
					options: {
						offset: [0, 0],
					},
				},
			],
		});
	}

	function show() {
		visible = !onClick;
	}

	function hide() {
		visible = false;
	}

	function handleClick() {
		if (onClick) {
			onClick();
			visible = true;
		}
	}
</script>

<div class="d-inline" on:mouseleave={hide} on:mouseenter={show} on:click={handleClick}>
	<div bind:this={element} class="d-inline cursor-pointer">
		<Icon key={icon} />
	</div>

	{#if visible}
		<div bind:this={content} class="tooltip bs-tooltip-top show" role="tooltip">
			<div class="tooltip-arrow" data-popper-arrow></div>
			<div class="tooltip-inner">
				<slot />
			</div>
		</div>
	{/if}
</div>