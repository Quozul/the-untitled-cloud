import type { Writable } from "svelte/store";
import { writable } from "svelte/store";
import { browser } from "$app/env";

export function createStoreEntry(name: string, defaultValue: any = null, storage = browser && localStorage): Writable<any> {
	const storageValue = storage && storage.getItem(name) || null;
	const value = storageValue ? JSON.parse(storageValue) : defaultValue;
	const token = writable(value);

	token.subscribe((val) => {
		if (storage) {
			return (storage[name] = JSON.stringify(val));
		}
	});

	return token;
}