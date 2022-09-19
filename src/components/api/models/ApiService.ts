import type { ApiProduct } from "$models/ApiProduct";
import type { ApiServiceState } from "$models/ApiServiceState";
import type { ApiSubscription } from "$models/ApiSubscription";

export type ApiService = {
	id: string | null; // ID of the service
	product: ApiProduct; // Product associated with the service
	tag: string | null; // Container's tag
	port: string | null; // Exposed port
	name: string | null; // Container's name
	hasFtpPassword: boolean;
	state: ApiServiceState;
	subscription: ApiSubscription; // Subscription associated with the service
};
