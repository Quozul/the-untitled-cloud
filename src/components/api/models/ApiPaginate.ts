export type ApiPaginate<T> = {
	data: T[],
	firstPage: boolean,
	lastPage: boolean,
	totalPage: number,
	totalElements: number,
	page: number,
}