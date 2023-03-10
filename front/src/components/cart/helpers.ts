import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiProduct } from "$models/ApiProduct";
import { api, getOptions, handleRequest } from "$shared/helpers";
import { get } from "svelte/store";
import { cart, clientSecret } from "$store/store";
import type { PromoCode } from "./models";
import type { ApiResponse } from "$shared/models";

export async function getProducts(page = 0): Promise<ApiResponse<ApiPaginate<ApiProduct>>> {
	const options = getOptions("GET");

	const params = new URLSearchParams();
	params.set("page", page.toString());

	const request = api(`product?${params.toString()}`, options);
	return await handleRequest<ApiPaginate<ApiProduct>>(request);
}

export async function getPromoCode(promoCode: string): Promise<ApiResponse<PromoCode>> {
	const request = api(`payment/stripe/promoCode/${promoCode}`, getOptions("GET"));
	return await handleRequest<PromoCode>(request);
}

/**
 * Add or remove a product from the cart
 * @param {ApiProduct} product The product to add or remove from the cart
 * @returns {boolean} Whether the product is in the cart or not
 */
export function toggleInCart(product: ApiProduct): boolean {
	const c = get(cart) ?? [];
	const isInCart = c.find((p) => p.id === product.id);

	if (!isInCart) {
		c.push(product);
	} else {
		removeFromCart(isInCart);
	}

	cart.set(c);
	clientSecret.set(null); // Reset client secret as the cart has changed

	return !isInCart;
}

export function removeFromCart(product: ApiProduct) {
	const c = get(cart) ?? [];
	const index = c.indexOf(product);

	if (index > -1) {
		c.splice(index, 1);
	}

	cart.set(c);
	clientSecret.set(null);
}
