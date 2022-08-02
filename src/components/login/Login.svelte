<script lang="ts">
	import { token } from "../../store/store";
	import { redirect, signIn } from "../../shared/helpers";

	let email, password;
	let error = false;
	let submitting = false;

	async function submit() {
		submitting = true;
		const res = await signIn(email, password);
		$token = res.token;

		await redirect();
		submitting = false;
	}
</script>

<style lang="scss">
	form {
		max-width: 330px;
		margin: auto;
	}
</style>

<form on:submit|preventDefault={submit}>
	{#if error}
		error
	{/if}
	<h4>Connexion</h4>
	<div class="mb-3">
		<label class="form-label">Adresse email</label>
		<input type="email" name="email" class="form-control" placeholder="example@example.com" bind:value={email}>
	</div>

	<div class="mb-3">
		<label class="form-label">Mot de passe</label>
		<input type="password" name="password" class="form-control" placeholder="Mot de passe" bind:value={password}>
	</div>

	<button type="submit" class="btn btn-primary" class:disabled={submitting}>
		{#if submitting}
			<span class="spinner-border spinner-border-sm"></span>
		{/if}
		Se connecter
	</button>
</form>