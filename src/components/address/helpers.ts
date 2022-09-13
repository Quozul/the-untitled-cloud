import type { ApiResponse } from "$shared/models";
import type { Address } from "./Address";
import { api, getOptions, handleRequest } from "$shared/helpers";

export async function getAddress(): Promise<ApiResponse<Address>> {
	const request = api("user/address", getOptions("GET"));
	return await handleRequest<Address>(request);
}

export async function setAddress(address: Address): Promise<void> {
	const request = api("user/address", getOptions("POST", address));
	await handleRequest(request);
}
