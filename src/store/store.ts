import type { Writable } from "svelte/store";
import type { Cart } from "../components/cart/models";
import type { CheckoutSteps } from "../components/checkout/constants";
import type { Server } from "../components/app/models";
import type { Credentials } from "../components/login/models/Credentials";
import { createStoreEntry } from "./helpers";
import { browser } from "$app/env";
import { LoginMode } from "../components/login/models/LoginMode";
import { ServerTab } from "../components/app/constants";

// Persistent store
export const token: Writable<string> = createStoreEntry("token", null, browser && localStorage);

// Session store
export const selectedServer: Writable<Server> = createStoreEntry("selectedServer", null, browser && sessionStorage);
export const cart: Writable<Cart> = createStoreEntry("cart", null, browser && sessionStorage);
export const sidebarCollapsed: Writable<boolean> = createStoreEntry("sidebarCollapsed", false, browser && sessionStorage);

// In memory store
export const credentials: Writable<Credentials> = createStoreEntry("credentials", LoginMode.LOGIN);
export const refreshServerInfo: Writable<boolean> = createStoreEntry("refreshServerInfo", false);
export const refreshServerList: Writable<boolean> = createStoreEntry("refreshServerList", false);
export const loginMode: Writable<LoginMode> = createStoreEntry("verification", LoginMode.LOGIN);
export const checkoutStep: Writable<CheckoutSteps> = createStoreEntry("checkoutStep", null);
export const clientSecret: Writable<string> = createStoreEntry("clientSecret", null);
export const selectedTab: Writable<ServerTab> = createStoreEntry("selectedTab", ServerTab.INFO);