import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiService } from "$models/ApiService";
import { ApiServiceStatus } from "../api/enums/ApiServiceStatus";
import { Products } from "../cart/constants";
import { ApiSubscriptionStatus } from "../api/enums/ApiSubscriptionStatus";
import { ApiSubscriptionProvider } from "../api/enums/ApiSubscriptionProvider";

export const EmptyPaginate: ApiPaginate<any> = {
	data: [],
	firstPage: true,
	lastPage: true,
	totalPage: 0,
	totalElements: 0,
	page: 0,
};

export const EmptyServer: ApiService = {
	id: "",
	product: {
		id: Products.Unknown,
		name: "",
		description: "",
	},
	tag: "latest",
	name: "",
	hasFtpPassword: true,
	port: null,
	state: {
		status: ApiServiceStatus.UNAVAILABLE,
		pending: false,
		created: false,
		running: false,
		starting: false,
		startedAt: null,
		finishedAt: null,
	},
	subscription: {
		id: "",
		status: ApiSubscriptionStatus.PENDING,
		provider: ApiSubscriptionProvider.STRIPE,
		creationDate: "1970-01-01T00:00:00.000000",
		deletionDate: null,
		active: true,
	},
};