import { getOptions, handleRequest } from "$shared/helpers";

/**
 * Asks for a password reset, must be called with a valid code.
 * @param code
 */
export async function deleteAccount(password: string, code: string): Promise<void> {
	const request = fetch(
		`${import.meta.env.VITE_API_BASE_URL}user`,
		getOptions("DELETE", { password, code })
	);
	await handleRequest(request);
}
