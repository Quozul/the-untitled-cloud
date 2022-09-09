import type { Products } from "$components/cart/constants";

export type ApiProduct = {
	id: Products,
	name: string,
	description: string,
	inStocks: boolean,
	price: number,
	cpu: number,
	memory: number,
}