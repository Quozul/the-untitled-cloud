package dev.quozul.payments.provider.stripe.models

import com.stripe.model.Customer
import kotlinx.serialization.Serializable

@Serializable
data class Address(
	/**
	 * The customer’s full name or business name.
	 */
	var fullname: String? = null,

	var city: String? = null,
	/**
	 * Two-letter country code ([ISO 3166-1alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)).
	 */
	var country: String? = null,

	/**
	 * Address line 1 (e.g., street, PO Box, or company name).
	 */
	var line1: String? = null,

	/**
	 * Address line 2 (e.g., apartment, suite, unit, or building).
	 */
	var line2: String? = null,

	/**
	 * ZIP or postal code.
	 */
	var postal_code: String? = null,

	/**
	 * State, county, province, or region.
	 */
	var state: String? = null,
) {
	companion object {
		fun fromStripeCustomer(customer: Customer): Address {
			val address = customer.address
			return Address(
				customer.name,
				address.city,
				address.country,
				address.line1,
				address.line2,
				address.postalCode,
				address.state,
			)
		}
	}
}