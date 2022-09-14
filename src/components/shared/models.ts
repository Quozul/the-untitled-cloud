import type { AuthenticationErrors } from "$components/login/models/AuthenticationErrors";

export type ApiError = {
	isError?: boolean;
	message?: string;
	code: AuthenticationErrors;
	translatedMessage?: string;
};

export type ClassNames = {
	[className: string]: boolean;
};

export type ApiResponse<T> = {
	error: ApiError | null;
	response: T | null;
};
