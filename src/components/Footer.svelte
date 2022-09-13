<script>
	import Icon from "./icons/Icon.svelte";
	import Link from "$shared/Link.svelte";
</script>

<footer class="py-5 mt-4">
	<div class="row row-cols-1 row-cols-sm-2 row-cols-md-5">
		<div class="flex-grow-1 col">
			<Link href="/" className="d-flex align-items-center mb-3 link-dark text-decoration-none">
				<Icon key="favicon" width="40" height="40" />
				<h4 class="ms-2 mb-0 fw-bold">
					The Untitled Cloud
				</h4>
			</Link>
			<p class="lead">Hébergeur de serveur de jeux.</p>
		</div>

		<div class="col">
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

		<div class="col">
			<h5>Infos</h5>
			<ul class="nav flex-column">
				<li class="nav-item mb-2">
					<Link href="/" className="nav-link p-0 text-muted">Mentions légales</Link>
				</li>
				<li class="nav-item mb-2">
					<Link href="/terms-of-use/" className="nav-link p-0 text-muted">Conditions générales</Link>
				</li>
			</ul>
		</div>
	</div>

	<div class="d-flex border-top pt-3 mt-3 justify-content-between">
		<p class="text-muted">© 2022 The Untitled Cloud. Tous droits réservés.</p>

		<div>
			<Link href="https://discord.gg/hbKkYVKeke" className="text-black">
				<Icon key="discord" />
			</Link>
		</div>
	</div>
</footer>
