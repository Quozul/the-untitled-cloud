<script lang="ts">
	import { user } from "$store/store";
	import { onMount } from "svelte";
	import { updateDiscordAccount } from "$components/app/helpers";
	import { page } from "$app/stores";
	import { goto } from "$app/navigation";

	let avatarUrl: string = null;

	onMount(async () => {
		const code = $page.url.searchParams.get("code");
		if (code !== null) {
			await updateDiscordAccount(code, window.location.origin + window.location.pathname);
			$page.url.searchParams.delete("code");
			await goto(`?${$page.url.searchParams.toString()}`);
		}

		if ($user.discord) {
			const extension = $user.discord.avatar.startsWith("a_") ? ".gif" : ".png";
			avatarUrl = `https://cdn.discordapp.com/avatars/${$user.discord.id}/${$user.discord.avatar}${extension}`;
		}
	});
</script>

{#if $user?.discord}
	<button class="btn btn-light py-0 discord">
		{#if avatarUrl}
			<img src={avatarUrl} alt="Avatar" class="avatar" />
		{/if}
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
