import countries from "$locales/en/countries.json";
import errors from "$locales/en/errors.json";
import front from "$locales/en//front.json";
import common from "$locales/en//common.json";
import dashboard from "$locales/en//dashboard.json";

export default {
	...front,
	...countries,
	...errors,
	...common,
	...dashboard,
}