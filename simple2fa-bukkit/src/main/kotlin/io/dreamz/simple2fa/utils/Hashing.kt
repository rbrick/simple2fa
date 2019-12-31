package io.dreamz.simple2fa.utils

import java.lang.StringBuilder
import java.security.MessageDigest

object Hashing {
    @JvmStatic
    open fun hash(algo: String, data: ByteArray): String {
        val digest = MessageDigest.getInstance(algo)
        val builder = StringBuilder()
        for (x in digest.digest(data))
            builder.append(String.format("%02x", x))
        return builder.toString()
    }
}