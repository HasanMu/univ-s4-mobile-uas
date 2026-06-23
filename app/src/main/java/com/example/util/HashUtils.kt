package com.example.util

import java.security.MessageDigest

object HashUtils {
    fun sha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input // Fallback to plain if hash error
        }
    }
}
