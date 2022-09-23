import type { ApiResponse } from "$shared/models";
import type { FileEntry } from "./models";
import { locale } from "svelte-intl-precompile";
import { get } from "svelte/store";
import { ftp, handleRequest } from "$shared/helpers";

export async function ftpGet(serverId: string, path: string[]): Promise<ApiResponse<FileEntry[]>> {
	const request = ftp(`${serverId}/${path.join("/")}`);
	return await handleRequest<FileEntry[]>(request);
}

export function date(timestamp: number): string {
	const date = new Date(timestamp * 1000);

	if (date.getDate() === new Date().getDate()) {
		return date.toLocaleTimeString(get(locale) as string, {
			hour: "numeric",
			minute: "numeric",
		});
	}

	return date.toLocaleDateString(get(locale) as string, {
		year: "numeric",
		month: "short",
		day: "numeric",
	});
}

/**
 * Format bytes as human-readable text.
 *
 * @param bytes Number of bytes.
 * @param si True to use metric (SI) units, aka powers of 1000. False to use
 *           binary (IEC), aka powers of 1024.
 * @param dp Number of decimal places to display.
 *
 * @return Formatted string.
 */
export function humanFileSize(bytes: number, si = false, dp = 1) {
	const thresh = si ? 1000 : 1024;

	if (Math.abs(bytes) < thresh) {
		return bytes + " B";
	}

	const units = si
		? ["kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"]
		: ["KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"];

	let u = -1;
	const r = 10 ** dp;

	do {
		bytes /= thresh;
		++u;
	} while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);

	return bytes.toFixed(dp) + " " + units[u];
}