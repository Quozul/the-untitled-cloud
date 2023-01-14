package dev.quozul.servers.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SizedIterable

@Serializable
data class Paginate<T>(
	val data: List<T>,
	val firstPage: Boolean,
	val lastPage: Boolean,
	val totalPages: Long,
	val totalElements: Long,
	val page: Int,
) {
	companion object {
		fun <T> fromSizedIterable(page: Int, size: Int, iterable: SizedIterable<T>): Paginate<T> {
			val count = iterable.count()
			val offset = (page * size).toLong()
			val lastPage = count <= (page + 1) * size

			return Paginate(
				iterable.limit(size, offset).toList(),
				firstPage = page == 0,
				lastPage = lastPage,
				totalPages = count / size,
				totalElements = count,
				page = page,
			)
		}
	}
}