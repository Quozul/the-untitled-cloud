import type { AuthenticationErrors } from "$components/login/models/AuthenticationErrors";

/**
 * @deprecated Any object using this is probably deprecated.
 */
export type Id = {
	id: string,
}

export type ApiError = {
	isError?: boolean,
	message?: string,
	code: AuthenticationErrors,
	translatedMessage?: string,
}

export type ClassNames = {
	[className: string]: boolean,
}