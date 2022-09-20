<script lang="ts">
	import Modal from "$components/modal/Modal.svelte";
	import { onMount } from "svelte";
	import { theme } from "../store/store";

	let errorModalVisible = false;
	let error: string | null = null;

	onMount(() => {
		window.onunhandledrejection = handleUnhandledRejection;
		window.onerror = handleError;
		document.body.setAttribute("data-bs-theme", $theme);
	});

	function handleError(...args) {
		console.log("Caught error");
		errorModalVisible = true;
		error = "Check console.";
	}

	function handleUnhandledRejection(event: PromiseRejectionEvent) {
		console.log("Caught unhandled rejection");
		errorModalVisible = true;
		error = event.reason;
	}

	function reload() {
		location.reload();
	}
</script>

<Modal
	bind:visible={errorModalVisible}
	onClick={reload}
	okText="Reload"
	closeText="Ignore"
	title="Oops, an error has occured.">
	<pre class="m-3">{error}</pre>
</Modal>

<slot />