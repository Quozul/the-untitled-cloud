/** @type {import('@sveltejs/kit').ParamMatcher} */
export function match(param: string) {
	return /^(fr|en)$/.test(param);
}