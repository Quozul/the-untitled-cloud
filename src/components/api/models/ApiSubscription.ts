import type { ApiSubscriptionStatus } from "$enums/ApiSubscriptionStatus";
import type { ApiSubscriptionProvider } from "$enums/ApiSubscriptionProvider";

export type ApiSubscription = {
	id: string;
	status: ApiSubscriptionStatus;
	provider: ApiSubscriptionProvider;
	creationDate: string;
	deletionDate: string | null;
	name: string | null;
};
