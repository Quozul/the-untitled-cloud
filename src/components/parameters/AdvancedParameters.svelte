<script>
	import { parameters } from "$store/store";
	import Link from "$shared/Link.svelte";
	import { t } from "svelte-intl-precompile";
</script>

<div class="d-flex flex-column gap-3">
	<div class="form-check">
		<input
			class="form-check-input"
			type="checkbox"
			id="flexCheckChecked"
			bind:checked={$parameters.useAikar}
		/>
		<label class="form-check-label" for="flexCheckChecked">
			{$t("parameters.use_the")}
			<Link href="https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/">
				{$t("parameters.aikar_flags")}
			</Link>
		</label>
	</div>

	<div class="d-flex gap-3">
		{#if !$parameters.useAikar}
			<div>
				<label for="jvmFlags" class="form-label">{$t("parameters.jvm_flags")}</label>
				<input
					id="jvmFlags"
					maxlength="1024"
					class="form-control"
					placeholder="-Xmx2G"
					bind:value={$parameters.jvmFlags}
				/>
			</div>
		{/if}
	</div>
</div>
