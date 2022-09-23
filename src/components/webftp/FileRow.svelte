<script lang="ts">
	import type { FileEntry } from "./models";
	import Icon from "$components/icons/Icon.svelte";
	import { createEventDispatcher } from "svelte";
	import { humanFileSize, date } from "$components/webftp/helpers";
	import { FileType } from "$components/webftp/models.js";
	import { t } from "svelte-intl-precompile";

	const dispatch = createEventDispatcher();

	export let file: FileEntry;

	function handleClick() {
		dispatch("click", file);
	}
</script>

<tr class="file-row" on:click|preventDefault={handleClick}>
	<th scope="row">
		<Icon key={file.file_type.toLowerCase()} />
	</th>
	<td>{file.name}</td>
	<td class="d-none d-lg-table-cell">
		{#if file.file_type === FileType.FILE}
			{humanFileSize(file.size)}
		{:else}
			{file.size}
			{#if file.size > 1}
				{$t("webftp.items")}
			{:else}
				{$t("webftp.item")}
			{/if}
		{/if}
	</td>
	<td class="d-none d-lg-table-cell">{date(file.time)}</td>
</tr>

<style lang="scss">
	.file-row {
		cursor: pointer;
		user-select: none;

		&:hover {
			background-color: $white;
		}
	}
</style>