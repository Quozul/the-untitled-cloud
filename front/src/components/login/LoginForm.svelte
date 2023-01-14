<script lang="ts">
	import Login from "./Login.svelte";
	import Signup from "./Signup.svelte";
	import { credentials, loginMode } from "$store/store";
	import { LoginMode } from "./models/LoginMode";
	import Verification from "./Verification.svelte";
	import { createEventDispatcher, onDestroy, onMount } from "svelte";
	import ForgotPassword from "./Password.svelte";
	import { t } from "svelte-intl-precompile";
	import { redirect } from "$shared/helpers";

	/**
	 * Set to null to disable redirection
	 */
	export let redirectTo: string | null = "/";
	export let defaultStyle = true;

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
			await redirect(redirectTo);
		}

		dispatch("submit");
	}
</script>

<div class:fixed-width={defaultStyle}>
	{#if $loginMode === LoginMode.VERIFICATION}
		<h4 class="mb-0" class:d-none={!defaultStyle}>{$t("authentication.verification")}</h4>
		<Verification on:submit={handleSubmit} {defaultStyle} />
	{:else if $loginMode === LoginMode.SIGNUP}
		<h4 class="mb-0" class:d-none={!defaultStyle}>{$t("authentication.signup")}</h4>
		<Signup on:submit={handleSubmit} {defaultStyle} />
	{:else if $loginMode === LoginMode.CHANGE_PASSWORD}
		<h4 class="mb-0" class:d-none={!defaultStyle}>{$t("authentication.password_change")}</h4>
		<ForgotPassword on:submit={handleSubmit} {defaultStyle} />
	{:else}
		<h4 class="mb-0" class:d-none={!defaultStyle}>{$t("authentication.login")}</h4>
		<Login on:submit={handleSubmit} {defaultStyle} />
	{/if}

	{#if $loginMode !== LoginMode.VERIFICATION && $loginMode !== LoginMode.CHANGE_PASSWORD}
		<button
			type="button"
			on:click={toggleMode}
			class="d-block btn btn-sm btn-link p-0 text-start"
		>
			{#if $loginMode === LoginMode.LOGIN}
				{$t("authentication.no_account_yet_question_mark")}
			{:else}
				{$t("authentication.already_have_account_question_mark")}
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
