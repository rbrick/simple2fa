package io.dreamz.simple2fa.utils

import io.dreamz.simple2fa.Simple2FA

// the URI format is like
// otpauth://TYPE/LABEL?PARAMETERS
const val FORMAT = "otpauth://%s/%s?%s"

class OTPAuthUri(val type: String,
                 val label: String,
                 val parameters: Map<String, String>) {
    override fun toString(): String {
        var encodedParameters = ""
        parameters.forEach { k, v -> encodedParameters = "$k=$v&$encodedParameters" }
        encodedParameters.dropLast(0)
        return FORMAT.format(type, label, encodedParameters)
    }
}

object KeyGenerator {
    @JvmStatic
    fun generate(): String {
        val randomBytes = ByteArray(40)
        Simple2FA.instance.random.nextBytes(randomBytes)
        Simple2FA.instance.random.setSeed(System.nanoTime())
        return Base32String.encode(randomBytes)
    }
}
