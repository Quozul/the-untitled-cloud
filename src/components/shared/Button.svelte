<script lang="ts">
    import { ButtonVariant } from "./constants";
    import Icon from "../icons/Icon.svelte";

    export let variant: ButtonVariant = ButtonVariant.PRIMARY;
    export let className: string = "";
	export let disabled: boolean = false;
	export let type: string = "button";
	export let onClick: VoidFunction = null;
    export let loading: boolean = false;
    export let icon: string = null;

	let processing = false;

	async function handleClick() {
		try {
			processing = true;
			await onClick();
		} finally {
			processing = false;
		}
	}
</script>

{#if loading}
    <div class="placeholder-glow">
        <button
            {type}
            class="placeholder disabled text-{variant} btn btn-{variant} d-inline-flex align-items-center {className}"
            disabled
            on:click|preventDefault={handleClick}
        >
            {#if processing}
                <span class="spinner-border spinner-border-sm me-2"></span>
            {:else if icon}
                <Icon key={icon} className="me-2"/>
            {/if}

            <slot/>
        </button>
    </div>
{:else}
    <button
        {type}
        class="btn btn-{variant} d-inline-flex align-items-center {className}"
        disabled="{disabled || processing}"
        on:click|preventDefault={handleClick}
    >
        {#if processing}
            <span class="spinner-border spinner-border-sm me-2"></span>
        {:else if icon}
            <Icon key={icon} className="me-2"/>
        {/if}

        <slot/>
    </button>
{/if}