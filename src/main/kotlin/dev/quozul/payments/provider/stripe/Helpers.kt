package dev.quozul.payments.provider.stripe

import com.stripe.exception.InvalidRequestException
import com.stripe.model.Customer
import com.stripe.param.CustomerCreateParams
import dev.quozul.authentication.User
import dev.quozul.authentication.Users
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun getStripeUser(uuid: UUID): Customer? {
	val stripeId = transaction {
		User.findById(uuid)?.stripeId
	}

	return try {
		Customer.retrieve(stripeId)
	} catch (_: InvalidRequestException) {
		null
	}
}

fun getOrCreateStripeCustomer(user: User): Customer {
	return if (user.stripeId != null) {
		Customer.retrieve(user.stripeId)
	} else {
		val params: CustomerCreateParams = CustomerCreateParams.builder()
			.setEmail(user.email)
			.build()

		val customer = Customer.create(params)

		transaction {
			user.stripeId = customer.id
		}

		customer
	}
}

/*
 * The user should never be null at this point.
 * But it could happen.
 */
fun getUserFromStripeId(id: String): User {
	return transaction {
		User.find {
			Users.stripeId eq id
		}.first()
	}
}