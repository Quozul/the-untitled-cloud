package dev.quozul.authentication

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
	INVALID_CODE("Invalid code");

	fun toHashMap(isError: Boolean = false): AuthenticationError {
		return AuthenticationError(isError, this.message, this.name)
	}
}