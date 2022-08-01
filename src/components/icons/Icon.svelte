<script>
    export let key;
    export let className = "me-2";
    export let width = "16";
    export let height = "16";
</script>

<svg class={className} {width} {height}>
    <use xlink:href="#{key}"></use>
</svg>