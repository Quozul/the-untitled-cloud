<script lang="ts">
	import type { FileEntry } from "./models";
	import { server } from "$store/store";
	import FileRow from "$components/webftp/FileRow.svelte";
	import Button from "$shared/Button.svelte";
	import { FileType } from "./models";
	import { t } from "svelte-intl-precompile";
	import PathEntry from "$components/webftp/PathEntry.svelte";
	import { ftpGet } from "./helpers";
	import { Variant } from "$shared/constants.js";

	let path: string[] = [];
	let files: FileEntry[] = [];
	let fetching: boolean = false;

	$: fetchFiles(path);

	async function fetchFiles(path: string[]) {
		fetching = true;
		const { response } = await ftpGet($server.id, path);
		files = response;
		fetching = false;
	}

	function goBack() {
		if (fetching) {
			return;
		}

		path.pop();
		path = path;
	}

	function handlePath(event) {
		if (fetching) {
			return;
		}

		const index = event.detail as number;
		path = path.slice(0, index + 1);
	}

	function handleClick(event) {
		if (fetching) {
			return;
		}

		const file = event.detail as FileEntry;

		if (file.file_type === FileType.DIRECTORY) {
			path.push(file.name);
			path = path;
		}
	}
</script>

<div class="bg-light p-4 d-flex element flex-column">
	<h4>WebFTP</h4>

	<p class="text-muted">
		{$t("webftp.instruction")}
	</p>

	<div class="d-flex align-items-center gap-2 mb-3">
		<Button icon="chevron-left" onClick={goBack} variant={Variant.NONE} outline={true} />

		<nav class="overflow-hidden">
			<ol class="breadcrumb mb-0 flex-nowrap">
				<PathEntry file="root" index={-1} last={path.length === 0} on:click={handlePath} />
				{#each path as file, index}
					<PathEntry {file} {index} last={index === path.length - 1} on:click={handlePath} />
				{/each}
			</ol>
		</nav>
	</div>

	<div class="table-responsive">
		<table class="table">
			<thead>
			<tr>
				<th scope="col">#</th>
				<th scope="col">Name</th>
				<th scope="col" class="d-none d-lg-table-cell">Size</th>
				<th scope="col" class="d-none d-lg-table-cell">Modified</th>
			</tr>
			</thead>

			<tbody>
			{#each files as file}
				<FileRow {file} on:click={handleClick} />
			{/each}
			</tbody>
		</table>
	</div>
</div>