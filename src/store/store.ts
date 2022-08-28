import type { Writable } from "svelte/store";
import type { Cart } from "$components/cart/models";
import type { CheckoutSteps } from "$components/checkout/constants";
import type { Credentials } from "$components/login/models/Credentials";
import { createStoreEntry } from "./helpers";
import { browser } from "$app/env";
import { LoginMode } from "$components/login/models/LoginMode";
import { ServerTab } from "$components/app/constants";
import { EmptyPaginate } from "$components/app/models";
import { writable } from "svelte/store";
import type { ApiError } from "$components/shared/models";
import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiService } from "$models/ApiService";
import type { ApiServer } from "$models/ApiServer";
import type { ApiUser } from "$models/ApiUser";

// Persistent store
export const token: Writable<string> = createStoreEntry("token", null, browser && localStorage);

// Session store
/**
 * @deprecated Use $server instead
 */
export const selectedServer: Writable<ApiService | null> = createStoreEntry("selectedServer", null, browser && sessionStorage);
export const cart: Writable<Cart> = createStoreEntry("cart", null, browser && sessionStorage);
export const sidebarCollapsed: Writable<boolean> = createStoreEntry("sidebarCollapsed", false, browser && sessionStorage);

// App active servers stores
export const servers: Writable<ApiPaginate<ApiService>> = createStoreEntry("servers", EmptyPaginate, browser && sessionStorage);
export const fetchingServers: Writable<boolean> = writable(false);
export const fetchServersError: Writable<ApiError | null> = writable(null);

// App current selected server stores
export const server: Writable<ApiService | null> = createStoreEntry("server", null, browser && sessionStorage);
export const fetchingServer: Writable<boolean> = writable(false);
export const fetchServerError: Writable<ApiError | null> = writable(null);

// App server parameters store
export const parameters: Writable<ApiServer> = createStoreEntry("parameters", null, browser && sessionStorage);

// App current user
export const user: Writable<ApiUser> = createStoreEntry("user", null, browser && sessionStorage);

// In memory store
export const credentials: Writable<Credentials | null> = writable(null);
export const loginMode: Writable<LoginMode> = writable(LoginMode.LOGIN);
export const checkoutStep: Writable<CheckoutSteps | null> = writable(null);
export const clientSecret: Writable<string | null> = writable(null);
export const selectedTab: Writable<ServerTab> = writable(ServerTab.INFO);
export const onProfilePage: Writable<boolean> = writable(false);