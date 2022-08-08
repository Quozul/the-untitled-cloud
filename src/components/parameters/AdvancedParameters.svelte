<script>
	import { parameters } from "$store/store";
	import Link from "$shared/Link.svelte";
</script>

<div class="d-flex flex-column gap-3">
	<div class="form-check">
		<input class="form-check-input" type="checkbox" id="flexCheckChecked" bind:checked={$parameters.useAikar}>
		<label class="form-check-label" for="flexCheckChecked">
			Utiliser les <Link href="https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/">flags d'Aikar</Link>
		</label>
	</div>

	<div class="d-flex gap-3">
		<div>
			<label for="tag" class="form-label">Environnement</label>
			<select class="form-select" id="tag" bind:value={$parameters.tag}>
				<option value="latest">Dernière</option>
				<option value="java17">Java 17</option>
				<option value="java17-graalvm-ce">GraalVM 17</option>
				<option value="java11">Java 11</option>
				<option value="java8-multiarch">Java 8</option>
			</select>
		</div>

		{#if !$parameters.useAikar}
			<div>
				<label for="jvmFlags" class="form-label">Flags JVM</label>
				<input id="jvmFlags" maxlength="1024" class="form-control" placeholder="-Xmx2G" bind:value={$parameters.jvmFlags}>
			</div>
		{/if}
	</div>
</div>