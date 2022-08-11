import type { ApiInvoice } from "$models/ApiInvoice";

export type ApiSubscriptionDetails = {
	startDate: string,
	currentPeriodStart: string,
	currentPeriodEnd: string,
	canceledAt: string | null,
	status: string,
	cancelAtPeriodEnd: boolean,
	paymentMethodType: string,
	paymentMethodLast4: string | null,
	latestInvoice: ApiInvoice,
}