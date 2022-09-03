<script lang="ts">
	import { onProfilePage, server, token, user } from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants";
	import * as UAParser from "ua-parser-js";
	import { onMount } from "svelte";
	import { getUser, updateDiscordAccount } from "$components/app/helpers";
	import { page } from "$app/stores";
	import { goto } from "$app/navigation";
	import DeleteAccount from "$components/profile/DeleteAccount.svelte";

	$server = null;
	$onProfilePage = true;

	let ua: UAParser.UAParserInstance;
	let avatarUrl: string = null;

	onMount(async () => {
		ua = new UAParser(navigator.userAgent);

		const code = $page.url.searchParams.get("code");
		if (code !== null) {
			await updateDiscordAccount(code, window.location.origin + window.location.pathname);
			$page.url.searchParams.delete("code");
			await goto(`?${$page.url.searchParams.toString()}`);
		}

		$user = await getUser();
		const extension = $user.discord.avatar.startsWith("a_") ? ".gif" : ".png";
		avatarUrl = `https://cdn.discordapp.com/avatars/${$user.discord.id}/${$user.discord.avatar}${extension}`;
	});
</script>

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

<div class="bg-light p-4 d-flex element flex-column">
	<h4>Profil</h4>
	<p class="d-flex align-items-center gap-2 lead">
		<Icon key="tools"/>
		Cette page est en cours de construction.
	</p>
	<p>
		Pour toute demande en rapport avec la gestion de vos données (suppression, modification, récupération, ...),
		veuillez entrer en contact par email à l'adresse <a href="mailto:contact@theuntitledcloud.com">contact@theuntitledcloud.com</a>.
	</p>

	<div>
		{#if $user?.discord}
			<button class="btn btn-light py-0 discord">
				<img src={avatarUrl} alt="Avatar" class="avatar"/>
				<div class="name">
					<span class="username">{$user.discord.username}</span>
					<span class="discriminator">#{$user.discord.discriminator}</span>
				</div>
			</button>
		{:else}
			<a
				class="btn btn-primary"
				rel="noreferrer noopener"
				href="https://discord.com/api/oauth2/authorize?client_id=1013410460873281577&redirect_uri=http%3A%2F%2Flocalhost%3A5173%2Fapp%2Fprofile%2F&response_type=code&scope=identify"
			>
				Lier mon compte Discord
			</a>
		{/if}

		<Button disabled>Télécharger mes données</Button>
		<DeleteAccount/>
	</div>
</div>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>Appareils connectés</h4>

	<p class="text-muted">
		Ci-dessous, retrouvez la liste des appareils connectés à votre compte.
	</p>

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
				<td>{ua.getOS().name} {ua.getOS().version} - {ua.getBrowser().name} {ua.getBrowser().version}</td>
				<td>---</td>
				<td>Aujourd'hui</td>
				<td>
					Cet appareil
				</td>
			</tr>
		{/if}
		<tr>
			<td>---</td>
			<td>---</td>
			<td>---</td>
			<td>
				<Button variant={Variant.DANGER} className="btn-sm" disabled>Déconnecter</Button>
			</td>
		</tr>
		</tbody>
	</table>
</div>