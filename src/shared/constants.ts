import { DateTimeFormatter } from "@js-joda/core";
import { Locale } from "@js-joda/locale_fr";

export const formatter = DateTimeFormatter
	.ofPattern("eeee d MMMM yyyy")
	.withLocale(Locale.FRANCE);

export const shortDate = DateTimeFormatter
	.ofPattern("d MMMM")
	.withLocale(Locale.FRANCE);