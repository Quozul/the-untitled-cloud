export type ApiInvoice = {
	periodStart: string;
	periodEnd: string;
	paid: boolean;
	amountDue: number;
	amountPaid: number;
	amountRemaining: number;
	total: number;
	currency: string;
};
