package io.dreamz.simple2fa.utils

import java.nio.ByteBuffer
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface Counter {
    fun count(): Long
}

class BasicCounter(var x: Long) : Counter {
    override fun count(): Long = x++
}

// interval = X
// step = t0
// https://tools.ietf.org/html/rfc6238#section-4.2
class TimeCounter(var interval: Int = 30, var step: Int = 0) : Counter {
    override fun count(): Long = Math.floor((Instant.now().epochSecond - step).toDouble() / interval).toLong()
}

// Implementation of https://tools.ietf.org/html/rfc4226#section-5.4
class HOTP(private val counter: Counter = TimeCounter(), private val tokenLen: Int = 6) {
    // HOTP is a truncation of the hashed message authentication code
    // HOTP = truncate(HMAC((K, C)))
    // Where K is the private key of the user and C is the counter to use.
    fun generate(algorithm: String, key: ByteArray): String = when {
        tokenLen < 6 -> throw IllegalArgumentException("token length is too small!")
        else -> {
            val macResult = hmac(algorithm, key)
            val code = macResult.truncate().rem(Math.pow(10.toDouble(), tokenLen.toDouble())).toInt()
            code.toString().padStart(tokenLen, '0')
        }
    }

    private fun ByteArray.truncate(): Int {
        //"Dynamic truncation" https://tools.ietf.org/html/rfc4226#section-5.4
        val offset = (this[this.size - 1].toInt() and 0xf)
        return (this[offset].toInt() and 0x7f shl 24
                or (this[offset + 1].toInt() and 0xff shl 16)
                or (this[offset + 2].toInt() and 0xff shl 8)
                or (this[offset + 3].toInt() and 0xff))
    }

    private fun hmac(algorithm: String, key: ByteArray): ByteArray {
        val hmacAlgo = "HMAC" + algorithm.toUpperCase()
        val mac = Mac.getInstance(hmacAlgo)
        mac.init(SecretKeySpec(key, ""))
        return mac.doFinal(ByteBuffer.allocate(8).putLong(counter.count()).array())
    }
}