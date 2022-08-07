<script lang="ts">
    import { t } from "svelte-intl-precompile";
	import { cart, token } from "$store/store";
    import Link from "../shared/Link.svelte";

	function addToCart() {
		$cart = {
			id: "",
			price: 200,
			name: $t("product.starter.name"),
			description: $t("product.starter.description"),
		}
	}
</script>

<div class="card">
    <div class="card-header py-4">
        <h4 class="my-0 fw-normal">{$t("product.starter.name")}</h4>
    </div>

    <div class="card-body">
        <p class="mb-0 text-muted fw-light">{$t("starting_from")}</p>
        <h1 class="card-title pricing-card-title">2€<small class="text-muted fw-light">/{$t("month")}</small>
        </h1>

        <ul class="list-unstyled mt-3">
            <li>2 {$t("unit.gigabyte")} {$t("of")} {$t("memory")}</li>
            <li>1 {$t("cpu_core")}</li>
        </ul>

        <Link onClick={addToCart} href={$token ? "/rent/profile/" : "/rent/login/"} className="w-100 btn btn-lg btn-outline-primary">
            {$t("choose")}
        </Link>
    </div>
</div>