<script lang="ts">
	import Modal from "$components/modal/Modal.svelte";
	import { token } from "$store/store";
	import { goto } from "$app/navigation";
	import { locale, t } from "svelte-intl-precompile";
	import { onMount } from "svelte";
	import jwtDecode from "jwt-decode";

	let tokenIsExpired = false;

	onMount(() => {
		const { exp } = jwtDecode($token);
		tokenIsExpired = Date.now() / 1000 > exp;
	});

	async function redirectToLogin() {
		$token = null;
		await goto(`/${$locale}/login/?redirect=/${$locale}/dashboard/`);
	}
</script>

<Modal
	visible={tokenIsExpired}
	icon="warning"
	title={$t("authentication.disconnected")}
	closeText={null}
	okText={$t("authentication.to_login")}
	onClick={redirectToLogin}
>
	<div class="p-3">{$t("authentication.please_login")}</div>
</Modal>
