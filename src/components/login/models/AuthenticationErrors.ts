export enum AuthenticationErrors {
	VERIFY_ACCOUNT = "VERIFY_ACCOUNT",
	INVALID_CREDENTIALS = "INVALID_CREDENTIALS",
	ACCEPT_TOS = "ACCEPT_TOS",
	INVALID_CODE = "INVALID_CODE",
	EXPIRED_CODE = "EXPIRED_CODE",

	// Form validation codes
	PASSWORDS_DIFFER = "PASSWORDS_DIFFER",
	EMPTY = "EMPTY",
}