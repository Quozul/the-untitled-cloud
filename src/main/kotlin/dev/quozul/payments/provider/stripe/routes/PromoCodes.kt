package dev.quozul.payments.provider.stripe.routes

import com.stripe.exception.StripeException
import com.stripe.model.PromotionCode
import com.stripe.param.PromotionCodeListParams
import dev.quozul.authentication.models.AuthenticationErrors
import dev.quozul.payments.provider.stripe.models.ApiPromoCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configurePromoCodes() {
	get("promoCode/{promoCode}") {
		val promoCode = try {
			call.parameters["promoCode"]!!
		} catch (e: NullPointerException) {
			call.response.status(HttpStatusCode.BadRequest)
			return@get
		}

		try {
			val params = PromotionCodeListParams.builder()
				.setCode(promoCode)
				.addExpand("data.coupon")
				.setLimit(1)
				.setActive(true)
				.build()
			val code = PromotionCode.list(params).data[0].coupon

			call.respond(
				ApiPromoCode(
					promoCode,
					code.amountOff?.toInt(),
					code.percentOff?.toInt(),
				)
			)
		} catch (e: StripeException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.INVALID_PROMO_CODE.toHashMap(true))
		} catch (e: IndexOutOfBoundsException) {
			call.response.status(HttpStatusCode.NotFound)
			call.respond(AuthenticationErrors.INVALID_PROMO_CODE.toHashMap(true))
		}
	}
}