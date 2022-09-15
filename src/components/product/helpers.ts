import type { ApiResponse } from "$shared/models";
import type { Stats } from "./models";
import { api, handleRequest } from "$shared/helpers";

export async function getStats(): Promise<ApiResponse<Stats>> {
	const request = api("product/stats");
	return await handleRequest(request);
}