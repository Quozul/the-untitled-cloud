package dev.quozul.products

import dev.quozul.database.models.Product
import dev.quozul.servers.models.Paginate
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.configureProductsRoutes() {
	get("") {
		val page = (call.request.queryParameters["page"] ?: "0").toInt()
		val size = (call.request.queryParameters["size"] ?: "6").toInt()
		val offset = (page * size).toLong();

		val (products, count) = transaction {
			val products = Product.all()

			Pair(products.limit(size, offset).map { it.toApiProduct() }, products.count())
		}

		val lastPage = count <= (page + 1) * size

		call.respond(Paginate(
			products,
			firstPage = page == 0,
			lastPage = lastPage,
			totalPages = count / size,
			totalElements = count,
			page = page,
		))
	}

	get("{productId}") {

	}
}