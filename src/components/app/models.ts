import type { ApiPaginate } from "$models/ApiPaginate";

export const EmptyPaginate: ApiPaginate<any> = {
	data: [],
	firstPage: true,
	lastPage: true,
	totalPage: 0,
	totalElements: 0,
	page: 0,
};

/**
 * @deprecated Use ApiSubscriptionDetails instead
 */
export type SubscriptionInfo = {
	startDate: string;
	currentPeriodStart: string;
	currentPeriodEnd: string;
	canceledAt: string | null;
	status: string;
	cancelAtPeriodEnd: boolean;
	paymentMethodType: string;
	paymentMethodLast4: string | null;
	latestInvoice: Invoice;
};

/**
 * @deprecated Use ApiInvoice instead
 */
export type Invoice = {
	periodStart: string;
	periodEnd: string;
	paid: boolean;
	amountDue: number;
	amountPaid: number;
	amountRemaining: number;
	total: number;
	currency: string;
};
