<script lang="ts">
	import { cart, promoCode } from "$store/store";
	import { formatPrice } from "$shared/helpers";
	import { t } from "svelte-intl-precompile";
	import CartRow from "./CartRow.svelte";
	import Button from "$shared/Button.svelte";
	import { Variant } from "$shared/constants.js";
	import Icon from "$components/icons/Icon.svelte";
	import { getPromoCode } from "./helpers";
	import type { ApiError } from "$shared/models";
	import { onMount } from "svelte";
	import { EmptyPromoCode } from "./constants";
	import Alert from "$shared/Alert.svelte";

	export let canEdit = true;

	let codeInput: string = null;
	let total = 0;
	let promotionCodeError: ApiError | null = null;

	$: {
		if ($cart) {
			total = $cart.reduce((a, b) => a + b.price, 0);

			if ($promoCode?.percentOff) {
				total -= (total * $promoCode.percentOff) / 100;
			} else if ($promoCode?.amountOff) {
				total -= $promoCode.amountOff;
			}
		}
	}

	onMount(async () => {
		if ($promoCode?.code) {
			await fetchCode();
		}
	});

	async function fetchCode() {
		promotionCodeError = null;

		const {error, response} = await getPromoCode(codeInput.toUpperCase());

		if (response) {
			$promoCode = response;
			codeInput = null;
		} else if (error) {
			$promoCode = EmptyPromoCode;
			promotionCodeError = error;
		}
	}

	async function setPromoCode() {
		if (!$promoCode) {
			$promoCode = EmptyPromoCode;
		}

		await fetchCode();
	}

	function removePromoCode() {
		$promoCode = EmptyPromoCode;
		promotionCodeError = null;
	}
</script>

<div>
	<h4 class="d-flex justify-content-between align-items-center mb-3">
		{$t("cart")}
	</h4>

	<ul class="list-group list-group-flush mb-3">
		{#if $cart?.length > 0}
			{#each $cart as product}
				<CartRow {product} {canEdit} />
			{/each}
		{:else}
			<li class="list-group-item d-flex justify-content-between lh-sm">
				<div>
					<h6 class="my-0">{$t("cart_is_empty")}</h6>
				</div>
			</li>
		{/if}

		{#if $promoCode && $promoCode.code && ($promoCode.amountOff || $promoCode.percentOff)}
			<li class="list-group-item d-flex justify-content-between lh-sm">
				<div>
					<div class="d-flex align-items-center gap-2">
						{#if canEdit}
							<Icon key="x-lg" onClick={removePromoCode} />
						{/if}
						<h6 class="my-0">{$promoCode.code}</h6>
					</div>
					<small class="text-muted">{$t("promo_code")}</small>
				</div>
				<span class="text-muted">
					-{#if $promoCode.amountOff}
						{formatPrice($promoCode.amountOff)}
					{:else if $promoCode.percentOff}
						{$promoCode.percentOff}%
					{:else}
						Erreur
					{/if}
				</span>
			</li>
		{/if}

		<li class="list-group-item d-flex justify-content-between border-0">
			<h6>{$t("sub_total")}</h6>
			<span>{formatPrice(total)}</span>
		</li>

		{#if $promoCode && $promoCode.code && ($promoCode.amountOff || $promoCode.percentOff)}
			<li class="list-group-item d-flex justify-content-between border-0">
				<h6>{$t("promotion")}</h6>
				<span>
					-{#if $promoCode.amountOff}
						{formatPrice($promoCode.amountOff)}
					{:else if $promoCode.percentOff}
						{$promoCode.percentOff}%
					{/if}
				</span>
			</li>
		{/if}

		<li class="list-group-item d-flex justify-content-between">
			<h6>{$t("taxes")}</h6>
			<span>{formatPrice(0)}</span>
		</li>

		<li class="list-group-item d-flex justify-content-between">
			<h5 class="fw-bold">{$t("total")}</h5>
			<strong class="fs-5">{formatPrice(total)}</strong>
		</li>
	</ul>

	{#if canEdit}
		<form class="card p-2">
			<div class="input-group">
				<input
					type="text"
					class="form-control"
					placeholder={$t("promo_code")}
					bind:value={codeInput}
				/>
				<Button type="submit" onClick={setPromoCode} variant={Variant.SECONDARY}>
					{$t("use")}
				</Button>
			</div>

			{#if promotionCodeError}
				<Alert variant={Variant.DANGER} className="mt-2 mb-0">
					{promotionCodeError.translatedMessage || promotionCodeError.message}
				</Alert>
			{/if}
		</form>
	{/if}
</div>
