import type { Id } from "../shared/models";

export type Cart = Id & {
	price: number,
	name: string,
	description: string,
}