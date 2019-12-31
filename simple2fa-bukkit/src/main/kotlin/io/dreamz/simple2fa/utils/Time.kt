package io.dreamz.simple2fa.utils

import java.util.concurrent.TimeUnit

object Time {
    @JvmStatic
    fun parseDuration(str: String): Long {
        // accept d(days), h (hours), m(minutes), s(seconds)
        var currentNumber = ""
        var currentTime = 0L
        for (c in str) {
            if (c.isDigit()) {
                currentNumber += c
            } else if (c.isLetter()) {
                currentTime += when {
                    c.toLowerCase() == 'd' -> (TimeUnit.DAYS.toMillis(currentNumber.toLong()))
                    c.toLowerCase() == 'h' -> (TimeUnit.HOURS.toMillis(currentNumber.toLong()))
                    c.toLowerCase() == 'm' -> (TimeUnit.MINUTES.toMillis(currentNumber.toLong()))
                    c.toLowerCase() == 's' -> (TimeUnit.SECONDS.toMillis(currentNumber.toLong()))
                    else -> 0L
                }
            } else {
                continue
            }
        }
        return currentTime
    }
}