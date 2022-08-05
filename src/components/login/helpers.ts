import type { ApiError } from "$shared/models";
import type { Token } from "./models/Token";
import { getOptions, handleResponse } from "$shared/helpers";

/**
 * Registers a new user
 * @param email
 * @param password
 * @param language
 * @param acceptTos
 */
export async function signUp(email: string, password: string, language: string, acceptTos: boolean): Promise<ApiError> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}authentication/signUp`,
		getOptions("POST", { email, password, language, acceptTos }),
	)
	return await handleResponse(request) as ApiError;
}

/**
 * Login the user, provide code if needed
 * @param email
 * @param password
 * @param code
 */
export async function signIn(email: string, password: string, code: string | null = null): Promise<Token> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}authentication/signIn`,
		getOptions("POST", { email, password, code }),
	);
	return await handleResponse(request) as Token;
}

/**
 * Sends a verification code to the given email address
 * @param email
 */
export async function sendVerificationCode(email: string): Promise<ApiError> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}authentication/code/${email}`,
		getOptions("POST"),
	);
	return await handleResponse(request) as ApiError;
}

/**
 * Asks for a password reset, must be called with a valid code.
 * @param email
 * @param password
 * @param code
 */
export async function changePassword(email: string, password: string, code: string): Promise<Token> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}authentication/password`,
		getOptions("POST", { email, password, code }),
	);
	return await handleResponse(request) as Token;
}