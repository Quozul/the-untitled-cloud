package dev.quozul.authentication

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

/*
 * Users
 */

object Users: UUIDTable() {
    val email = varchar("email", 256).uniqueIndex()
    val password = char("password", 64)
    val stripeId = varchar("stripe_id", 255).uniqueIndex().nullable() // External foreign key to Stripe
    val tosAcceptDate = datetime("tos_accept_date").defaultExpression(CurrentDateTime())
    val verificationCode = char("verification_code", 6)
    val verificationCodeValidDate = datetime("verification_code_valid_date")
    val communicationLanguage = char("communication_language", 2)
    val emailVerified = bool("email_verified").default(false)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var password by Users.password
    var email by Users.email
    var stripeId by Users.stripeId
    var tosAcceptDate by Users.tosAcceptDate
    var verificationCode by Users.verificationCode
    var verificationCodeValidDate by Users.verificationCodeValidDate
    var communicationLanguage by Users.communicationLanguage
    var emailVerified by Users.emailVerified
}