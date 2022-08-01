<script>
	import { token } from "../../store/store.ts";

	let email, password;
	let error = false;

	async function submit() {
		fetch(`${ import.meta.env.VITE_API_BASE_URL }/authentication/signIn`, {
			method: "POST",
			headers: new Headers({ "content-type": "application/json" }),
			body: JSON.stringify({
				email, password,
			}),
		})
			.then(res => {
				error = !res.ok;
				return res.json();
			})
			.then(json => {
				$token = json.token;
			});
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

	<button type="submit" class="btn btn-primary">Submit</button>
</form>