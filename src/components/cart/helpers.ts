import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiProduct } from "$models/ApiProduct";
import { api, getOptions, handleRequest } from "$shared/helpers";
import { get } from "svelte/store";
import { cart } from "$store/store";
import type { PromoCode } from "./models";

export async function getProducts(page: number = 0): Promise<ApiPaginate<ApiProduct>> {
	const options = getOptions("GET");

	const params = new URLSearchParams();
	params.set("page", page.toString());

	const request = api(`product?${params.toString()}`, options);
	return (await handleRequest(request)) as ApiPaginate<ApiProduct>;
}

export async function getPromoCode(promoCode: string): Promise<PromoCode> {
	const request = api(`payment/stripe/subscription/promoCode/${promoCode}`, getOptions("GET"));
	return (await handleRequest(request)) as PromoCode;
}

export function toggleInCart(product: ApiProduct) {
	const c = get(cart) ?? [];
	const isInCart = c.find((p) => p.id === product.id);

	if (!isInCart) {
		c.push(product);
	} else {
		removeFromCart(isInCart);
	}
	cart.set(c);
}

export function removeFromCart(product: ApiProduct) {
	const c = get(cart) ?? [];
	const index = c.indexOf(product);
	if (index > -1) {
		c.splice(index, 1);
	}
	cart.set(c);
}

export function getCartTotal() {}
