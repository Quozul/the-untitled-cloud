import type { PromoCode } from "./models";

export const EmptyPromoCode: PromoCode = {
	code: null,
	amountOff: null,
	percentOff: null,
}

export enum Products {
	MinecraftServer = "4068d064-85c6-459c-a482-6106e4f6bbfb",
	SteamServer = "84ad0c99-0fe5-4ff3-a643-abef8b37c653",
}