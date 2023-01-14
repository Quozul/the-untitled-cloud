export type Address = {
	/**
	 * The customer’s full name or business name.
	 */
	fullname: string | null;

	city: string | null;
	/**
	 * Two-letter country code ([ISO 3166-1alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)).
	 */
	country: string | null;

	/**
	 * Address line 1 (e.g., street, PO Box, or company name).
	 */
	line1: string | null;

	/**
	 * Address line 2 (e.g., apartment, suite, unit, or building).
	 */
	line2: string | null;

	/**
	 * ZIP or postal code.
	 */
	postal_code: string | null;

	/**
	 * State, county, province, or region.
	 */
	state: string | null;
};
