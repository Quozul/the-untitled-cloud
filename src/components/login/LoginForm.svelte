<script lang="ts">
	import Login from "./Login.svelte";
	import Signup from "./Signup.svelte";
	import { credentials, loginMode } from "../../store/store";
	import { LoginMode } from "./models/LoginMode";
	import Verification from "./Verification.svelte";
	import { onDestroy, onMount } from "svelte";

	export let redirectTo: string = "/";

	onMount(() => {
		$loginMode = LoginMode.LOGIN;
    });

	onDestroy(() => {
		// Remove credentials from memory for security
		$credentials = null;
    });

	function toggleMode() {
		if ($loginMode === LoginMode.SIGNUP) {
			$loginMode = LoginMode.LOGIN;
		} else if ($loginMode === LoginMode.LOGIN) {
			$loginMode = LoginMode.SIGNUP;
        }
	}
</script>

<style lang="scss">
	div {
		max-width: 330px;
		margin: auto;
	}
</style>

<div>
    {#if $loginMode === LoginMode.VERIFICATION}
        <Verification {redirectTo}/>
    {:else if $loginMode === LoginMode.SIGNUP}
        <Signup {redirectTo}/>
    {:else}
        <Login {redirectTo}/>
    {/if}

    {#if $loginMode !== LoginMode.VERIFICATION}
        <button type="button" on:click={toggleMode} class="d-block btn btn-sm btn-link p-0">
            {#if $loginMode === LoginMode.LOGIN}
                Vous n'avez pas encore de compte? S'inscrire.
            {:else}
                Vous avez déjà un compte? Se connecter.
            {/if}
        </button>
    {/if}
</div>