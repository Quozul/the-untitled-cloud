import { api, getOptions, handleRequest } from "$shared/helpers";
import type { ApiResponse } from "$shared/models";

/**
 * Asks for a password reset, must be called with a valid code.
 * @param password
 * @param code
 */
export async function deleteAccount(password: string, code: string): Promise<ApiResponse<void>> {
	const request = api(`user`, getOptions("DELETE", { password, code }));
	return await handleRequest(request);
}
