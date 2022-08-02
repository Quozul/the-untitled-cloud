<script>
	import ServerInfo from "../../components/app/ServerInfo.svelte";
	import Sidebar from "../../components/app/Sidebar.svelte";
	import { fetchedServers } from "../../store/store";
</script>

<div class="d-flex vh-100">
    <Sidebar/>

    <div class="overflow-auto flex-grow-1 p-3">
        {#if $fetchedServers}
            <ServerInfo/>
        {/if}
    </div>
</div>