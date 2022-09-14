<script>
	import { t } from "svelte-intl-precompile";
	import Icon from "./icons/Icon.svelte";
	import Link from "$shared/Link.svelte";
</script>

<footer class="py-5 mt-4">
	<div class="row row-cols-1 row-cols-sm-2 row-cols-md-5">
		<div class="flex-grow-1 col">
			<Link
				href="/"
				className="d-flex align-items-center mb-3 link-dark text-decoration-none"
			>
				<Icon key="favicon" width="40" height="40" />
				<h4 class="ms-2 mb-0 fw-bold">The Untitled Cloud</h4>
			</Link>
			<p class="lead">{$t("game_server_host")}</p>
		</div>

		<div class="col">
			<h5>{$t("pages")}</h5>
			<ul class="nav flex-column">
				<li class="nav-item mb-2">
					<Link href="/" className="nav-link p-0 text-muted">{$t("home")}</Link>
				</li>
				<li class="nav-item mb-2">
					<Link href="/products/" className="nav-link p-0 text-muted">
						{$t("products")}
					</Link>
				</li>
				<li class="nav-item mb-2">
					<Link href="/checkout/" className="nav-link p-0 text-muted">
						{$t("checkout")}
					</Link>
				</li>
			</ul>
		</div>

		<div class="col">
			<h5>{$t("infos")}</h5>
			<ul class="nav flex-column">
				<li class="nav-item mb-2">
					<Link href="/legal-mentions/" className="nav-link p-0 text-muted">
						{$t("legal_mentions")}
					</Link>
				</li>
				<li class="nav-item mb-2">
					<Link href="/terms-of-use/" className="nav-link p-0 text-muted">
						{$t("general_terms")}
					</Link>
				</li>
			</ul>
		</div>
	</div>

	<div class="d-flex border-top pt-3 mt-3 justify-content-between">
		<p class="text-muted">© 2022 The Untitled Cloud. {$t("all_right_reserved")}</p>

		<div class="d-flex gap-3">
			<Link href="https://discord.gg/hbKkYVKeke" className="text-black">
				<Icon key="discord" />
			</Link>

			<Link href="https://github.com/the-untitled-cloud" className="text-black">
				<Icon key="github" />
			</Link>
		</div>
	</div>
</footer>
