package dev.quozul.payments.provider.stripe

import com.stripe.model.Customer
import com.stripe.param.CustomerCreateParams
import dev.quozul.authentication.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun getStripeUser(uuid: UUID): Customer? {
	val stripeId = transaction {
		User.findById(uuid)?.stripeId
	}
	return Customer.retrieve(stripeId)
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