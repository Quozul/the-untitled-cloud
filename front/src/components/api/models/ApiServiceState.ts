import type { ApiServiceStatus } from "$enums/ApiServiceStatus";

export type ApiServiceState = {
	status: ApiServiceStatus;
	pending: boolean;
	created: boolean;
	running: boolean;
	starting: boolean;
	startedAt: string | null;
	finishedAt: string | null;
};
