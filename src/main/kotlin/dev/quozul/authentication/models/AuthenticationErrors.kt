package dev.quozul.authentication.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationError(
	val isError: Boolean,
	val message: String,
	val code: String,
)

enum class AuthenticationErrors(private val message: String) {
	VERIFY_ACCOUNT("Verify account"),
	INVALID_CREDENTIALS("Invalid credentials"),
	ACCEPT_TOS("You must accept the TOS"),
	EXPIRED_CODE("Code expired"),
	CANCEL_ERROR("Cancel error"),
	NO_CONTAINER("Container not found"),
	STRIPE_ERROR("Stripe error"),
	NOT_PAID("Not paid invoices cannot be cancelled"),
	NOT_MODIFIED("Not modified"),
	ACTION_NOT_ALLOWED("Not allowed to make action"),
	ALREADY_EXISTS("An account already exists"),
	INVALID_PROMO_CODE("Invalid promotion code"),
	ALREADY_OWNS("Already owns a product"),
	HAVE_ACTIVE_SUBSCRIPTION("User have active subscriptions"),
	INVALID_CODE("Invalid code");

	fun toHashMap(isError: Boolean = false): AuthenticationError {
		return AuthenticationError(isError, this.message, this.name)
	}
}