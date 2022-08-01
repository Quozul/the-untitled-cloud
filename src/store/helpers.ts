import type { Writable } from "svelte/store";
import { writable } from "svelte/store";
import { browser } from "$app/env";

export function createStoreEntry(name: string, defaultValue: any = null, storage = browser && localStorage): Writable<any> {
	const token = writable((storage && storage.getItem(name)) ?? defaultValue);
	token.subscribe((val) => {
		if (storage) return (storage[name] = val);
	});
	return token;
}