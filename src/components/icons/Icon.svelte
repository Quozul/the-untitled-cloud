<script lang="ts">
    export let key: string = "void";
    export let className: string = "";
    export let width: string = "16";
    export let height: string = "16";
    export let onClick: VoidFunction | null = null;
</script>

<svg class="icon {className}" {width} {height} on:click|preventDefault={onClick}>
    <use xlink:href="#{key}"></use>
</svg>