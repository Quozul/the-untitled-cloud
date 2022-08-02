<script lang="ts">
	import Login from "./Login.svelte";
	import Signup from "./Signup.svelte";

	export let redirectTo: string = "/";
	let mode = true;

	function toggleMode() {
		mode = !mode;
	}
</script>

<style lang="scss">
	div {
		max-width: 330px;
		margin: auto;
	}
</style>

<div>
    {#if mode}
        <Login {redirectTo}/>
    {:else}
        <Signup {redirectTo}/>
    {/if}

    <button type="button" on:click={toggleMode} class="d-block btn btn-sm btn-link p-0">
        {#if mode}
            Vous n'avez pas encore de compte? S'inscrire.
        {:else}
            Vous avez déjà un compte? Se connecter.
        {/if}
    </button>
</div>