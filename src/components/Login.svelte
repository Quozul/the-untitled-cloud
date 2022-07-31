<script>
	import { token } from "../store.js";

	let email;
	let password;
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

<form on:submit|preventDefault={submit}>
	{#if error}
		error
	{/if}
	<div class="mb-3">
		<label class="form-label">Email address</label>
		<input type="email" name="email" class="form-control" bind:value={email}>
	</div>

	<div class="mb-3">
		<label class="form-label">Password</label>
		<input type="password" name="password" class="form-control" bind:value={password}>
	</div>

	<button type="submit" class="btn btn-primary">Submit</button>

	<small>
		Vous n'avez pas encore de compte? S'inscrire.
	</small>
</form>