<script>
	import Icon from "./icons/Icon.svelte";
    import Link from "$shared/Link.svelte";
</script>

<footer class="row row-cols-1 row-cols-sm-2 row-cols-md-5 py-5 border-top mt-4">
    <div class="flex-grow-1 col mb-3">
        <Link href="/" className="d-flex align-items-center mb-3 link-dark text-decoration-none">
            <Icon key="box" width="40" height="40"/>
        </Link>
        <p class="text-muted">© 2022 Quozul. Tous droits réservés.</p>
    </div>

    <div class="col mb-3">
        <h5>Pages</h5>
        <ul class="nav flex-column">
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">Accueil</a></li>
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">Contact</a></li>
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">A propos</a></li>
        </ul>
    </div>

    <div class="col mb-3">
        <h5>Infos</h5>
        <ul class="nav flex-column">
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">Politique de Confidentialité</a></li>
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">CGU</a></li>
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">Mentions légales</a></li>
            <li class="nav-item mb-2"><a href="/" class="nav-link p-0 text-muted">Gestion des cookies</a></li>
        </ul>
    </div>
</footer>