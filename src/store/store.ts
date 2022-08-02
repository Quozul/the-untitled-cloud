import { createStoreEntry } from "./helpers";
import { CheckoutSteps } from "../components/checkout/constants";
import { browser } from "$app/env";

export const token = createStoreEntry("token");

export const checkoutStep = createStoreEntry("checkoutStep", CheckoutSteps.LOGIN, browser && sessionStorage);
export const clientSecret = createStoreEntry("clientSecret", null, browser && sessionStorage);
export const selectedServer = createStoreEntry("selectedServer", null, browser && sessionStorage);
export const fetchedServers = createStoreEntry("fetchedServers", false, browser && sessionStorage);