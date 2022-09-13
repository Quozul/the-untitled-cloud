<script>
	import { t } from "svelte-intl-precompile";
	import Icon from "$components/icons/Icon.svelte";
	import Link from "$shared/Link.svelte";
</script>

<div class="rounded-5 bg-light p-lg-5 my-lg-5 my-3">
	<h2 class="fw-bolder">Toujours pas convaincu ?</h2>

	<p class="lead d-flex align-items-center gap-2">
		<Icon key="percent" />
		<span>
			Utilisez le code “<strong>WELCOME</strong>” pour avoir 10% de réduction sur votre premier achat !
		</span>
	</p>

	<Link href="/rent/products/" className="btn btn-dark rounded-pill px-4 me-md-2">
		{$t("rent_a_server")}
	</Link>
</div>