import type { AuthenticationErrors } from "../components/login/models/AuthenticationErrors";

export type Id = {
	id: string,
}

export type ApiError = {
	isError: boolean,
	message: string,
	code: AuthenticationErrors,
}