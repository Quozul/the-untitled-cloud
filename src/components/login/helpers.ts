import type { ApiError, ApiResponse } from "$shared/models";
import type { Token } from "./models/Token";
import { api, getOptions, handleRequest } from "$shared/helpers";

/**
 * Registers a new user
 * @param email
 * @param password
 * @param language
 * @param acceptTos
 */
export async function signUp(
	email: string,
	password: string,
	language: string,
	acceptTos: boolean
): Promise<ApiResponse<ApiError>> {
	const request = api(
		`authentication/signUp`,
		getOptions("POST", { email, password, language, acceptTos })
	);
	return await handleRequest<ApiError>(request);
}

/**
 * Login the user, provide code if needed
 * @param email
 * @param password
 * @param code
 */
export async function signIn(
	email: string,
	password: string,
	code: string | null = null
): Promise<ApiResponse<Token>> {
	const request = api(
		`authentication/signIn`,
		getOptions("POST", { email, password, code })
	);
	return await handleRequest<Token>(request);
}

/**
 * Sends a verification code to the given email address
 * @param email
 */
export async function sendVerificationCode(email: string): Promise<ApiResponse<ApiError>> {
	const request = api(
		`authentication/code/${email}`,
		getOptions("POST")
	);
	return await handleRequest<ApiError>(request);
}

/**
 * Asks for a password reset, must be called with a valid code.
 * @param email
 * @param password
 * @param code
 */
export async function changePassword(
	email: string,
	password: string,
	code: string
): Promise<ApiResponse<Token>> {
	const request = api(
		`authentication/password`,
		getOptions("POST", { email, password, code })
	);
	return await handleRequest<Token>(request);
}
