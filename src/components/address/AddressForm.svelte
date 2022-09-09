<script lang="ts">
    import { locale, t } from "svelte-intl-precompile";
	import type { Address } from "./Address";
	import type { ApiError } from "$shared/models";
	import { onMount } from "svelte";
	import { token, cart } from "$store/store";
	import Icon from "$components/icons/Icon.svelte";
	import jwtDecode from "jwt-decode";
	import { getAddress, setAddress } from "$shared/helpers";
	import Button from "$shared/Button.svelte";
    import Link from "$shared/Link.svelte";
    import { goto } from "$app/navigation";

	let address: Address = {
		city: null,
		country: null,
		fullname: null,
		line1: null,
		line2: null,
		postal_code: null,
		state: null,
	};
	let email = "";

	onMount(async () => {
        const jwt = jwtDecode($token);
		email = jwt.email;

		try {
			const response = await getAddress();

			// If the user has an address
			if (response) {
				address = response;
            }
		} catch (e: ApiError) {
			// TODO: Handle error
            console.log(e);
		}
	});

	async function submit() {
		try {
            await setAddress(address);
            await goto($cart ? `/${$locale}/rent/checkout/` : `/${$locale}/rent/products/`);
		} catch (e: ApiError) {
			// TODO: Handle error
			console.log(e);
		}
	}
</script>

<h4 class="mb-1">{$t("billing_address")}</h4>

<div class="fs-6 mb-3">
    {$t("logged_in_as")}&nbsp;
    <div class="text-muted d-inline">
        {email}
    </div>
</div>

<form>
    <div class="row g-3">
        <div class="col-sm-12">
            <label for="firstName" class="form-label">{$t("full_name")}</label>
            <input type="text" class="form-control" id="firstName" placeholder={$t("full_name")} required="" bind:value={address.fullname}>
        </div>

        <div class="col-12">
            <label for="address" class="form-label">{$t("address.line1")}</label>
            <input type="text" class="form-control" id="address" placeholder={$t("address.line1")} required="" bind:value={address.line1}>
        </div>

        <div class="col-12">
            <label for="address2" class="form-label">
                {$t("address.line2")}
                <span class="text-muted">({$t("optional")})</span>
            </label>
            <input type="text" class="form-control" id="address2" placeholder={$t("address.line2")} bind:value={address.line2}>
        </div>

        <div class="col-md-5">
            <label for="country" class="form-label">{$t("address.country")}</label>
            <select class="form-select" id="country" required="" bind:value={address.country}>
                <option value="FR" selected>France</option>
            </select>
        </div>

        <div class="col-md-4">
            <label for="city" class="form-label">{$t("address.city")}</label>
            <input type="text" class="form-control" name="city" id="city" placeholder={$t("address.city")} bind:value={address.city}>
        </div>

        <div class="col-md-3">
            <label for="zip" class="form-label">{$t("address.postal_code")}</label>
            <input type="text" class="form-control" id="zip" placeholder={$t("address.postal_code")} required="" bind:value={address.postal_code}>
        </div>
    </div>

    <Button type="submit" className="btn btn-primary mt-3" onClick={submit}>
        {$t("save")}
    </Button>

    <Link href={$cart ? "/rent/checkout/" : "/rent/products/"} className="btn btn-secondary mt-3 d-inline-flex align-items-center gap-2">
        {$t("skip")}
        <Icon key="chevron-double-right"/>
    </Link>
</form>