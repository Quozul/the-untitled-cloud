<script lang="ts">
	import { onProfilePage, server, user } from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import Button from "$shared/Button.svelte";
	import { onMount } from "svelte";
	import { getUser } from "$components/app/helpers";
	import DeleteAccount from "$components/profile/DeleteAccount.svelte";

	$server = null;
	$onProfilePage = true;

	onMount(async () => {
		$user = await getUser();
	});
</script>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>Profil</h4>
	<p class="d-flex align-items-center gap-2 lead">
		<Icon key="tools" />
		Cette page est en cours de construction.
	</p>
	<p>
		Pour toute demande en rapport avec la gestion de vos données (suppression, modification,
		récupération, ...), veuillez entrer en contact par email à l'adresse <a
			href="mailto:contact@theuntitledcloud.com">contact@theuntitledcloud.com</a
		>.
	</p>

	<div>
		<Button disabled>Télécharger mes données</Button>
		<DeleteAccount />
	</div>
</div>

<style lang="scss">
	.discord {
		display: inline-flex;
		align-items: center;

		.avatar {
			width: 32px;
			height: 32px;
			border-radius: 16px;
		}

		.name {
			padding: 4px 0 4px 8px;
			margin-right: 4px;
			display: flex;
			flex-direction: column;
			font-size: 14px;

			.username {
				font-weight: 600;
			}

			.discriminator {
				font-weight: 400;
			}
		}
	}
</style>
