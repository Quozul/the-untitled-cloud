import { api, getOptions, handleRequest } from "$shared/helpers";

/**
 * Asks for a password reset, must be called with a valid code.
 * @param password
 * @param code
 */
export async function deleteAccount(password: string, code: string): Promise<void> {
	const request = api(
		`user`,
		getOptions("DELETE", { password, code })
	);
	await handleRequest(request);
}
