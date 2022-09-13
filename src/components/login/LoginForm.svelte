<script lang="ts">
	import Login from "./Login.svelte";
	import Signup from "./Signup.svelte";
	import { credentials, loginMode } from "$store/store";
	import { LoginMode } from "./models/LoginMode";
	import Verification from "./Verification.svelte";
	import { createEventDispatcher, onDestroy, onMount } from "svelte";
	import ForgotPassword from "./Password.svelte";
	import { t } from "svelte-intl-precompile";

	/**
	 * Set to null to disable redirection
	 */
	export let redirectTo: string | null = "/";
	export let defaultStyle: boolean = true;

	const dispatch = createEventDispatcher();

	onMount(() => {
		$loginMode = LoginMode.LOGIN;
	});

	onDestroy(() => {
		// Remove credentials from memory for security
		$credentials = null;
	});

	function toggleMode() {
		if ($loginMode === LoginMode.SIGNUP) {
			$loginMode = LoginMode.LOGIN;
		} else if ($loginMode === LoginMode.LOGIN) {
			$loginMode = LoginMode.SIGNUP;
		}
	}

	async function handleSubmit() {
		if (redirectTo) {
			await require(redirectTo);
		}

		dispatch("submit");
	}
</script>

<div class:fixed-width={defaultStyle}>
	{#if $loginMode === LoginMode.VERIFICATION}
		<Verification on:submit={handleSubmit} />
	{:else if $loginMode === LoginMode.SIGNUP}
		<Signup on:submit={handleSubmit} />
	{:else if $loginMode === LoginMode.CHANGE_PASSWORD}
		<ForgotPassword on:submit={handleSubmit} />
	{:else}
		<Login on:submit={handleSubmit} />
	{/if}

	{#if $loginMode !== LoginMode.VERIFICATION && $loginMode !== LoginMode.CHANGE_PASSWORD}
		<button type="button" on:click={toggleMode} class="d-block btn btn-sm btn-link p-0">
			{#if $loginMode === LoginMode.LOGIN}
				{$t("no_account_yet_question_mark")}
			{:else}
				{$t("already_have_account_question_mark")}
			{/if}
		</button>
	{/if}
</div>

<style lang="scss">
	.fixed-width {
		max-width: 330px;
		margin: auto;
	}
</style>
