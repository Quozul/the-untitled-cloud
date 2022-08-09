package dev.quozul.products

import dev.quozul.database.models.Product
import dev.quozul.servers.models.Paginate
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.configureProductsRoutes() {
	get("") {
		// TODO: Paginate
		val products = transaction {
			Product.all().map { it.toApiProduct() }
		}

		call.respond(Paginate(
			products,
			firstPage = true,
			lastPage = true,
			totalPages = 1,
			totalElements = products.size.toLong(),
			page = 0,
		))
	}
}