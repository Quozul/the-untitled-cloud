import type { ApiError } from "$components/shared/models";
import type { ApiPaginate } from "$models/ApiPaginate";
import type { ApiProduct } from "$models/ApiProduct";
import type { ApiServer } from "$models/ApiServer";
import type { ApiService } from "$models/ApiService";
import type { ApiUser } from "$models/ApiUser";
import type { CheckoutSteps } from "$components/checkout/constants";
import type { Credentials } from "$components/login/models/Credentials";
import type { PromoCode } from "$components/cart/models";
import type { Writable } from "svelte/store";

import { createStoreEntry } from "./helpers";
import { browser } from "$app/environment";
import { LoginMode } from "$components/login/models/LoginMode";
import { ServerTab } from "$components/app/constants";
import { EmptyPaginate, EmptyServer } from "$components/app/models";
import { writable } from "svelte/store";
import { EmptyPromoCode } from "$components/cart/constants";

// Persistent store
export const token: Writable<string | null> = createStoreEntry(
	"token",
	null,
	browser && localStorage
);
export const theme: Writable<string> = createStoreEntry("theme", "light", browser && localStorage);

// Session store
/**
 * @deprecated Use $server instead
 */
export const selectedServer: Writable<ApiService | null> = createStoreEntry(
	"selectedServer",
	null,
	browser && sessionStorage
);
export const cart: Writable<ApiProduct[]> = createStoreEntry("cart", [], browser && sessionStorage);
export const promoCode: Writable<PromoCode | null> = createStoreEntry(
	"promoCode",
	EmptyPromoCode,
	browser && sessionStorage
);
export const sidebarCollapsed: Writable<boolean> = createStoreEntry(
	"sidebarCollapsed",
	false,
	browser && sessionStorage
);

// App active servers stores
export const servers: Writable<ApiPaginate<ApiService>> = createStoreEntry(
	"servers",
	EmptyPaginate,
	browser && sessionStorage
);
export const fetchingServers: Writable<boolean> = writable(true);
export const fetchServersError: Writable<ApiError | null> = writable(null);

// App current selected server stores
export const server: Writable<ApiService | null> = createStoreEntry(
	"server",
	EmptyServer, // This might not be a good idea
	browser && sessionStorage
);
export const fetchingServer: Writable<boolean> = writable(false);
export const fetchServerError: Writable<ApiError | null> = writable(null);

// App server parameters store
export const parameters: Writable<ApiServer | null> = createStoreEntry(
	"parameters",
	null,
	browser && sessionStorage
);

// App current user
export const user: Writable<ApiUser | null> = createStoreEntry(
	"user",
	null,
	browser && sessionStorage
);

// In memory store
export const credentials: Writable<Credentials | null> = writable(null);
export const loginMode: Writable<LoginMode> = writable(LoginMode.LOGIN);
export const checkoutStep: Writable<CheckoutSteps | null> = writable(null);
export const onProfilePage: Writable<boolean> = writable(false);
export const cartModalVisible: Writable<boolean> = writable(false);
export const clientSecret: Writable<string | null> = writable(null);
