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
    val HEX_CHARS = "0123456789ABCDEF"
    val bytes = MessageDigest
        .getInstance(type)
        .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
}
