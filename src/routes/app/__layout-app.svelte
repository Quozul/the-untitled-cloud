<script>
    import Icons from "../../components/Icons.svelte";
	import Sidebar from "../../components/app/Sidebar.svelte";

    let error = false;
</script>

<Icons/>
<div class="d-flex vh-100">
    <Sidebar/>

    <div class="overflow-auto flex-grow-1 p-3">
        <slot/>
    </div>
</div>