<script lang="ts">
	import { server } from "$store/store";
</script>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>FTP</h4>

	<p class="text-muted">
		Téléchargez un client FTP de votre choix puis utilisez les informations ci-dessous pour vous
		connecter.
	</p>

	<dl class="d-flex flex-column m-0">
		<div class="separation">
			<dt>Adresse du serveur FTP</dt>
			<dd>
				<pre class="mb-0">141.94.98.122</pre>
			</dd>
		</div>

		<div class="separation">
			<dt>Identifiant</dt>
			<dd>L'adresse mail que vous utilisez pour vous connecter au site.</dd>
		</div>

		<div class="separation">
			<dt>Mot de passe</dt>
			<dd>Le même que pour vous connecter au site.</dd>
		</div>

		<div class="separation">
			{#if !$server}
				<p class="placeholder-glow w-100 m-0">
					<span class="placeholder h-100 col-12" />
				</p>
			{:else}
				<dt>Répertoire</dt>
				<dd>
					{$server.id}
				</dd>
			{/if}
		</div>
	</dl>
</div>

<style lang="scss">
	.element {
		flex: 1;
	}

	.separation {
		padding: 1em 0;
		display: flex;
		flex-direction: column;

		+ .separation {
			border-top: solid 1px black;
		}

		dd {
			margin: 0;
			flex-grow: 1;
		}
	}

	@include media-breakpoint-up(lg) {
		.separation {
			flex-direction: row;
		}

		dd {
			text-align: end;
		}
	}
</style>
