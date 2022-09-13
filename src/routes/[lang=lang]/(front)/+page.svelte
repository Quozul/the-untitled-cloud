<script>
	import { t } from "svelte-intl-precompile";
	import Hero from "$components/hero/Hero.svelte";
	import Numbers from "$components/hero/Numbers.svelte";
	import Feature from "$components/hero/Feature.svelte";
	import Link from "$shared/Link.svelte";
	import NotConvinced from "$components/hero/NotConvinced.svelte";
</script>

<svelte:head>
	<title>Accueil</title>
</svelte:head>

<div class="px-4 d-flex flex-column g-5">
	<div class="row flex-lg-row align-items-center g-5 my-lg-5 my-3">
		<div class="col-lg-6">
			<Hero />
		</div>
	</div>

	<Numbers />

	<div class="row g-3 justify-content-between my-lg-5 my-3">
		<div class="col-12 col-lg-6 d-flex gap-4 flex-column">
			<Feature title="Click & play" icon="controller">
				Quam exuviae dicere socio iuste angustias dicere Ptolomaeo Ptolomaeo ob quam nostri nostri Catonem
				piget.
			</Feature>

			<Feature title="Service client" icon="info">
				Primum Parthicarum rerum captu specie pro minantium ut animo gentium quibus rerum consilia captu
				gentium.
			</Feature>
		</div>

		<div class="col-12 col-lg-6 d-flex flex-column align-items-start justify-content-between">
			<div>
				<h2 class="fw-bolder">Serveur disponible dans la minute</h2>

				<p>
					Primi igitur omnium statuuntur Epigonus et Eusebius ob nominum gentilitatem oppressi. praediximus
					enim
					Montium sub ipso vivendi termino his vocabulis appellatos fabricarum culpasse tribunos ut adminicula
					futurae molitioni pollicitos.
				</p>
			</div>

			<Link href="/products/" className="btn btn-dark rounded-pill px-4 me-md-2">
				{$t("rent_a_server")}
			</Link>
		</div>
	</div>

	<NotConvinced/>
</div>
