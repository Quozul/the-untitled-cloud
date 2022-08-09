package dev.quozul.database.helpers

import dev.quozul.database.models.User
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ApiUser(
	val subscriptions: List<ApiSubscription>,

	val id: String, // TODO: Use UUID type
	val email: String,
	val tosAcceptDate: LocalDateTime,
	val communicationLanguage: String, // TODO: Use Locale type
	val isEmailVerified: Boolean,
) {
	companion object {
		fun fromUserId(id: UUID): ApiUser {
			val user = transaction {
				User.findById(id)!!
			}

			val subscriptions = ApiSubscription.fromOwner(user)

			return ApiUser(
				subscriptions,
				user.id.toString(),
				user.email,
				LocalDateTime.parse(user.tosAcceptDate.toString()), // TODO: Find better way to convert this
				user.communicationLanguage,
				user.emailVerified,
			)
		}
	}
}