<script>
	import Number from "$components/hero/Number.svelte";
</script>

<div class="container my-lg-5 my-3 d-none d-lg-block">
	<div class="row">
		<Number text="Serveurs loués" number="10+" />
		<Number text="Joueurs" number="20+" />
		<Number text="Jeux disponibles" number="2" />
	</div>
</div>

<style lang="scss">
	.container {
		margin-right: auto;
		margin-left: auto;
	}
</style>
