import type { ApiServiceState } from "$models/ApiServiceState";
import type { ApiSubscription } from "$models/ApiSubscription";
import type { ApiProductInfo } from "$models/ApiProduct";

export type ApiService = {
	id: string | null; // ID of the service
	product: ApiProductInfo; // Product associated with the service
	tag: string | null; // Container's tag
	port: string | null; // Exposed port
	name: string | null; // Container's name
	hasFtpPassword: boolean;
	state: ApiServiceState;
	subscription: ApiSubscription; // Subscription associated with the service
};

export type ApiServiceInfo = {
	id: string;
	name: string | null;
	productName: string;
	active: boolean;
	pending: boolean;
	cancelled: boolean;
	suspended: boolean;
	available: boolean;
};
