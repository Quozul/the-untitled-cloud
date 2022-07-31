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
    val username = varchar("username", 32).uniqueIndex().nullable()
    val email = varchar("email", 256).uniqueIndex()
    val password = char("password", 64)
    val stripeId = varchar("stripe_id", 255).uniqueIndex().nullable() // External foreign key to Stripe
    val tosAcceptDate = datetime("tos_accept_date").defaultExpression(CurrentDateTime())
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var username by Users.username
    var password by Users.password
    var email by Users.email
    var stripeId by Users.stripeId
    var tosAcceptDate by Users.tosAcceptDate
}