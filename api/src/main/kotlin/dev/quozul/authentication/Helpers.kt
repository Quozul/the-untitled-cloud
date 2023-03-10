package dev.quozul.authentication

import java.security.MessageDigest

/**
 * Supported algorithms on Android:
 *
 * Algorithm
 * MD5
 * SHA-1
 * SHA-224
 * SHA-256
 * SHA-384
 * SHA-512
 */
fun hashString(type: String, input: String): String {
    val hexChars = "0123456789abcdef"
    val bytes = MessageDigest
        .getInstance(type)
        .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(hexChars[i shr 4 and 0x0f])
        result.append(hexChars[i and 0x0f])
    }

    return result.toString()
}

fun generateValidationCode(): String {
    return (1..6)
        .map { ('0'..'9').random() }
        .joinToString("")
}