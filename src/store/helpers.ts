import type { Writable } from "svelte/store";
import { writable } from "svelte/store";
import { browser } from "$app/env";

export function createStoreEntry(name: string, defaultValue: any = null, storage: Storage | false = false): Writable<any> {
	const storageValue = storage && storage.getItem(name) || null;
	const value = storageValue ? JSON.parse(storageValue) : defaultValue;
	const token = writable(value);

	token.subscribe((val) => {
		if (storage && val !== undefined) {
			return (storage[name] = JSON.stringify(val));
		}
	});

	return token;
}