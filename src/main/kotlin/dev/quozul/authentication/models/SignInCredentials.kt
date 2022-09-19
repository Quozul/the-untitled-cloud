package dev.quozul.authentication.models

import kotlinx.serialization.Serializable

@Serializable
data class SignInCredentials(
    val email: String,
    val password: String,
    val code: String?,
)

@Serializable
data class SignUpCredentials(
    val email: String,
    val password: String,
    val language: String,
    val acceptTos: Boolean,
)

@Serializable
data class VerificationCredentials(
    val password: String,
    val code: String,
)

@Serializable
data class PasswordCredentials(
    val password: String,
)