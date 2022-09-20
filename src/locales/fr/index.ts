import countries from "$locales/fr/countries.json";
import errors from "$locales/fr/errors.json";
import front from "./front.json";
import common from "./common.json";
import dashboard from "./dashboard.json";

export default {
	...front,
	...countries,
	...errors,
	...common,
	...dashboard,
}