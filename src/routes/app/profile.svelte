<script lang="ts">
	import { onProfilePage, server } from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import Button from "$shared/Button.svelte";
	import { ButtonVariant } from "$shared/constants";

	$server = null;
	$onProfilePage = true;
</script>

<h1>Profil</h1>
<p class="d-flex align-items-center gap-2 lead">
	<Icon key="tools"/> Cette page est en cours de construction.
</p>

<div>
	<Button disabled>Télécharger mes données</Button>
	<Button variant={ButtonVariant.DANGER} disabled>Supprimer mon compte</Button>
</div>