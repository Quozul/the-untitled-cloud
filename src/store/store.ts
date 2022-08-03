import type { Writable } from "svelte/store";
import type { Cart } from "../components/cart/models";
import type { CheckoutSteps } from "../components/checkout/constants";
import type { Server } from "../components/app/models";
import type { Credentials } from "../components/login/models/Credentials";
import { createStoreEntry } from "./helpers";

import { browser } from "$app/env";
import { LoginMode } from "../components/login/models/LoginMode";

// Persistent store
export const token: Writable<string> = createStoreEntry("token");

// Session store
export const checkoutStep: Writable<CheckoutSteps> = createStoreEntry("checkoutStep", null, browser && sessionStorage);
export const clientSecret: Writable<string> = createStoreEntry("clientSecret", null, browser && sessionStorage);
export const selectedServer: Writable<Server> = createStoreEntry("selectedServer", null, browser && sessionStorage);
export const cart: Writable<Cart> = createStoreEntry("cart", null, browser && sessionStorage);
export const loginMode: Writable<LoginMode> = createStoreEntry("verification", LoginMode.LOGIN, browser && sessionStorage);

// In memory store
export const credentials: Writable<Credentials> = createStoreEntry("credentials", LoginMode.LOGIN);