<script lang="ts">
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";
	import * as UAParser from "ua-parser-js";
	import { onMount } from "svelte";

	let ua: UAParser.UAParserInstance;

	onMount(async () => {
		ua = new UAParser(navigator.userAgent);
	});
</script>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>Appareils connectés</h4>

	<p class="text-muted">Ci-dessous, retrouvez la liste des appareils connectés à votre compte.</p>

	<table class="table align-middle">
		<thead>
			<tr>
				<th>Appareil</th>
				<th>Adresse IP</th>
				<th>Dernière connexion</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			{#if ua}
				<tr>
					<td
						>{ua.getOS().name}
						{ua.getOS().version} - {ua.getBrowser().name}
						{ua.getBrowser().version}</td
					>
					<td>---</td>
					<td>Aujourd'hui</td>
					<td> Cet appareil </td>
				</tr>
			{/if}
			<tr>
				<td>---</td>
				<td>---</td>
				<td>---</td>
				<td>
					<Button variant={Variant.DANGER} className="btn-sm" disabled>Déconnecter</Button
					>
				</td>
			</tr>
		</tbody>
	</table>
</div>
