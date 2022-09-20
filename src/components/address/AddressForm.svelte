<script lang="ts">
	import { t } from "svelte-intl-precompile";
	import type { Address } from "./Address";
	import type { SelectItem } from "$components/select/SelectItem";
	import { createEventDispatcher, onMount } from "svelte";
	import { token } from "$store/store";
	import jwtDecode from "jwt-decode";
	import { getAddress, setAddress } from "./helpers";
	import Button from "$shared/Button.svelte";
	import Select from "$components/select/Select.svelte";
	import { EmptyAddress, IsoCountries } from "./constants";

	// Constants
	const dispatch = createEventDispatcher();

	// State
	let address: Address = EmptyAddress;
	let email = "";
	let value: SelectItem;

	$: value = { value: address.country, label: $t(`country.${address.country}`) };

	onMount(async () => {
		const jwt = jwtDecode($token);
		email = jwt.email;

		// TODO: Handle error
		const { response } = await getAddress();

		// If the user has an address
		if (response) {
			address = response;
		}
	});

	async function submit() {
		// TODO: Handle error
		await setAddress(address);
		dispatch("submit");
	}

	function selectCountry(event) {
		address.country = event.detail.value;
	}

	const items = IsoCountries.map((i) => ({ label: $t(`address.country.${i}`), value: i }));
</script>

<div class="mb-3">
	{$t("authentication.logged_in_as")}&nbsp;
	<div class="text-muted d-inline">
		{email}
	</div>
</div>

<form>
	<div class="row g-3">
		<div class="col-sm-12">
			<label for="firstName" class="form-label">{$t("address.full_name")}</label>
			<input
				type="text"
				class="form-control"
				id="firstName"
				placeholder={$t("address.full_name")}
				required=""
				bind:value={address.fullname}
			/>
		</div>

		<div class="col-12">
			<label for="address" class="form-label">{$t("address.line1")}</label>
			<input
				type="text"
				class="form-control"
				id="address"
				placeholder={$t("address.line1")}
				required=""
				bind:value={address.line1}
			/>
		</div>

		<div class="col-12">
			<label for="address2" class="form-label">
				{$t("address.line2")}
				<span class="text-muted">({$t("optional")})</span>
			</label>
			<input
				type="text"
				class="form-control"
				id="address2"
				placeholder={$t("address.line2")}
				bind:value={address.line2}
			/>
		</div>

		<div class="col-md-4">
			<label class="form-label" for="search-country">{$t("address.country")}</label>
			<Select
				{items}
				{value}
				className="w-100"
				on:select={selectCountry}
				placeholder={$t("search_country")}
				id="search-country"
			/>
		</div>

		<div class="col-md-5">
			<label for="city" class="form-label">{$t("address.city")}</label>
			<input
				type="text"
				class="form-control"
				name="city"
				id="city"
				placeholder={$t("address.city")}
				bind:value={address.city}
			/>
		</div>

		<div class="col-md-3">
			<label for="zip" class="form-label">{$t("address.postal_code")}</label>
			<input
				type="text"
				class="form-control"
				id="zip"
				placeholder={$t("address.postal_code")}
				required=""
				bind:value={address.postal_code}
			/>
		</div>

		<div class="col-12">
			<Button type="submit" className="w-100" onClick={submit}>
				{$t("common.next")}
			</Button>
		</div>
	</div>
</form>
