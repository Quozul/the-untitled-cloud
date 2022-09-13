<script>
	import Icon from "./icons/Icon.svelte";
	import Link from "$shared/Link.svelte";
</script>

<footer class="row row-cols-1 row-cols-sm-2 row-cols-md-5 py-5 border-top mt-4">
	<div class="flex-grow-1 col mb-3">
		<Link href="/" className="d-flex align-items-center mb-3 link-dark text-decoration-none">
			<Icon key="favicon" width="40" height="40" />
		</Link>
		<p class="text-muted">© 2022 The Untitled Cloud. Tous droits réservés.</p>
	</div>

	<div class="col mb-3">
		<h5>Pages</h5>
		<ul class="nav flex-column">
			<li class="nav-item mb-2">
				<Link href="/" className="nav-link p-0 text-muted">Accueil</Link>
			</li>
			<li class="nav-item mb-2">
				<Link href="/" className="nav-link p-0 text-muted">Contact</Link>
			</li>
			<li class="nav-item mb-2">
				<Link href="/" className="nav-link p-0 text-muted">A propos</Link>
			</li>
		</ul>
	</div>

	<div class="col mb-3">
		<h5>Infos</h5>
		<ul class="nav flex-column">
			<li class="nav-item mb-2">
				<Link href="/" className="nav-link p-0 text-muted">
					Politique de Confidentialité
				</Link>
			</li>
			<li class="nav-item mb-2">
				<Link href="/terms-of-use/" className="nav-link p-0 text-muted">CGU</Link>
			</li>
			<li class="nav-item mb-2">
				<Link href="/terms-of-sale/" className="nav-link p-0 text-muted">CGV</Link>
			</li>
			<li class="nav-item mb-2">
				<Link href="/" className="nav-link p-0 text-muted">Mentions légales</Link>
			</li>
			<li class="nav-item mb-2">
				<Link href="/" className="nav-link p-0 text-muted">Gestion des cookies</Link>
			</li>
		</ul>
	</div>
</footer>
