import countries from "$locales/fr/countries.json";
import errors from "$locales/fr/errors.json";
import front from "$locales/fr/front.json";
import common from "$locales/fr/common.json";
import dashboard from "$locales/fr/dashboard.json";

export default {
	...front,
	...countries,
	...errors,
	...common,
	...dashboard,
};
