import { createStoreEntry } from "./helpers";
import { CheckoutSteps } from "../components/checkout/constants";
import { browser } from "$app/env";
import type { Writable } from "svelte/store";
import type { Server } from "../components/app/models";

// Persistent store
export const token: Writable<string> = createStoreEntry("token");

// Session store
export const checkoutStep: Writable<CheckoutSteps> = createStoreEntry("checkoutStep", CheckoutSteps.LOGIN, browser && sessionStorage);
export const clientSecret: Writable<string> = createStoreEntry("clientSecret", null, browser && sessionStorage);
export const selectedServer: Writable<Server> = createStoreEntry("selectedServer", null, browser && sessionStorage);