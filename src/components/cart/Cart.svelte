<script lang="ts">
	import { cart, promoCode, cartModalVisible } from "$store/store";
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
	import Link from "$shared/Link.svelte";

	export let canEdit = true;
	export let background = false;

	let codeInput: string = null;
	let subtotal = 0;
	let total = 0;
	let promotionCodeError: ApiError | null = null;

	$: {
		if ($cart) {
			subtotal = $cart.reduce((a, b) => a + b.price, 0);

			if ($promoCode?.percentOff) {
				total = subtotal - (subtotal * $promoCode.percentOff) / 100;
			} else if ($promoCode?.amountOff) {
				total = subtotal - $promoCode.amountOff;
			}
		}
	}

	onMount(async () => {
		if ($promoCode?.code) {
			await fetchCode($promoCode.code);
		}
	});

	async function fetchCode(code: string) {
		promotionCodeError = null;

		const { error, response } = await getPromoCode(code.toUpperCase());

		if (response) {
			$promoCode = response;
			codeInput = null;
		} else if (error) {
			$promoCode = EmptyPromoCode;
			promotionCodeError = error;
		}
	}

	async function setPromoCode() {
		await fetchCode(codeInput);
	}

	function removePromoCode() {
		$promoCode = EmptyPromoCode;
		promotionCodeError = null;
	}

	function closeCartModal() {
		$cartModalVisible = false;
	}
</script>

<div class="d-flex gap-4 flex-column py-3" class:bg-light={background}>
	<ul class="list-group list-group-flush px-4">
		{#if $cart?.length > 0}
			{#each $cart as product}
				<CartRow {product} {canEdit} {background} />
			{/each}
		{:else}
			<li class="list-group-item d-flex justify-content-between lh-sm" class:bg-light={background}>
				<div class="d-flex flex-column align-items-center w-100 gap-2">
					<Icon key="bag-plus" height="32" width="32"/>
					<h6 class="my-0">{$t("cart_is_empty")}</h6>
					<p class="text-muted">{$t("add_a_product")}</p>
					<Link href="/products/" onClick={closeCartModal} className="btn btn-dark">{$t("add_product")}</Link>
				</div>
			</li>
		{/if}
	</ul>

	{#if $cart?.length > 0}
		<hr class="m-0" />

		<div class="d-flex gap-4 flex-column px-4">
			{#if canEdit}
				<form autocomplete="off">
					<label for="promoCode" class="form-label">{$t("promo_code")}</label>
					<div class="input-group">
						<input
							type="text"
							class="form-control"
							id="promoCode"
							placeholder={$t("promo_code")}
							required=""
							bind:value={codeInput}
						/>

						<Button type="submit" onClick={setPromoCode} variant={Variant.DARK} disabled={!codeInput?.length}>
							{$t("use")}
						</Button>
					</div>
				</form>

				{#if promotionCodeError}
					<Alert variant={Variant.DANGER} className="mt-2 mb-0">
						{promotionCodeError.translatedMessage || promotionCodeError.message}
					</Alert>
				{/if}
			{/if}

			<div class="d-flex justify-content-between border-0">
				<h6 class="text-muted mb-0">{$t("sub_total")}</h6>
				<span class="fw-bold">{formatPrice(subtotal)}</span>
			</div>

			{#if $promoCode && $promoCode.code && ($promoCode.amountOff || $promoCode.percentOff)}
				<div class="d-flex justify-content-between border-0">
					<div class="d-flex align-items-center gap-2">
						<h6 class="mb-0 text-muted">{$t("promotion")}</h6>
						<span class="badge text-bg-secondary rounded-pill d-flex align-items-center gap-1">
							{$promoCode.code}
							<Icon key="x-lg" className="cursor-pointer" onClick={removePromoCode} height="10" width="10" />
						</span>
					</div>

					<span class="fw-bold">
						-{#if $promoCode.amountOff}
							{formatPrice($promoCode.amountOff)}
						{:else if $promoCode.percentOff}
							{$promoCode.percentOff}%
						{/if}
					</span>
				</div>
			{/if}

			<div>
				<div class="d-flex justify-content-between">
				<h6 class="text-muted mb-0">{$t("taxes")}</h6>
				<span class="fw-bold">{formatPrice(0)}</span>
				</div>
				<small class="text-muted">TVA non applicable, <Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000042159618/">art. 293 B du CGI</Link>.</small>
			</div>

			<hr class="m-0" />

			<div class="d-flex justify-content-between">
				<h5 class="fw-bold mb-0">{$t("total")}</h5>
				<strong class="fs-5">{formatPrice(total)}</strong>
			</div>
		</div>
	{/if}
</div>
