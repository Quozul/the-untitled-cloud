<style lang="scss">
	.list-group.list-group-radio {
		.form-check-input {
			z-index: 2;
			margin-top: -.5em;

			&:checked + .list-group-item {
				background-color: var(--bs-body);
				border-color: var(--bs-blue);
				box-shadow: 0 0 0 2px var(--bs-blue);
			}
		}

		.list-group-item {
			cursor: pointer;
		}
	}
</style>

<div class="list-group list-group-radio d-grid gap-2 border-0 w-auto">
	<div class="position-relative">
		<input class="form-check-input position-absolute top-50 end-0 me-3 fs-5" type="radio" name="listGroupRadioGrid" id="listGroupRadioGrid1" value checked>
		<label class="list-group-item py-3 pe-5" for="listGroupRadioGrid1">
			<strong class="fw-semibold">Vanilla</strong>
			<span class="d-block small opacity-75">Pour une aventure sans modifications</span>
		</label>
	</div>

	<div class="position-relative">
		<input class="form-check-input position-absolute top-50 end-0 me-3 fs-5" type="radio" name="listGroupRadioGrid" id="listGroupRadioGrid2" value>
		<label class="list-group-item py-3 pe-5" for="listGroupRadioGrid2">
			<strong class="fw-semibold">Plugins</strong>
			<span class="d-block small opacity-75">Parfait pour les serveurs communautaires</span>
		</label>
	</div>

	<div class="position-relative">
		<input class="form-check-input position-absolute top-50 end-0 me-3 fs-5" type="radio" name="listGroupRadioGrid" id="listGroupRadioGrid3" value>
		<label class="list-group-item py-3 pe-5" for="listGroupRadioGrid3">
			<strong class="fw-semibold">Moddé</strong>
			<span class="d-block small opacity-75">Pour s'amuser entre amis</span>
		</label>
	</div>

	<div class="position-relative">
		<input class="form-check-input position-absolute top-50 end-0 me-3 fs-5" type="radio" name="listGroupRadioGrid" id="listGroupRadioGrid4" value>
		<label class="list-group-item py-3 pe-5" for="listGroupRadioGrid4">
			<strong class="fw-semibold">Autre</strong>
			<span class="d-block small opacity-75">Autres logiciels de serveur</span>
		</label>
	</div>
</div>