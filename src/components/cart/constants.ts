import type { PromoCode } from "./models";

export const EmptyPromoCode: PromoCode = {
	code: null,
	amountOff: null,
	percentOff: null,
};

export enum Products {
	MinecraftServer = "MINECRAFT",
	ArkServer = "ARK",
}
