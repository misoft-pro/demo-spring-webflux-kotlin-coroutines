package pro.misoft.poc.springreactive.kotlin.business.model.common

import java.security.SecureRandom
import java.util.*
import kotlin.math.floor

object IdGenerator {

    fun uniqueId(): String {
        return UUID.randomUUID().toString()
    }

    fun uniqueNumber(): String {
        return generate(16, true)
    }

    fun uniqueNumberOfLength(length: Int): String {
        return generate(length, true)
    }

    private fun generate(length: Int, numbersOnly: Boolean): String {
        val strTable = if (numbersOnly) "1234567890" else "1234567890abcdefghijkmnpqrstuvwxyz"
        val len = strTable.length
        var bDone = true
        val retStr = StringBuilder()
        do {
            var count = 0
            for (i in 0 until length) {
                val dblR = SecureRandom().nextDouble() * len
                val intR = floor(dblR).toInt()
                val c = strTable[intR]
                if (('0' <= c) && (c <= '9')) {
                    count++
                }
                retStr.append(strTable[intR])
            }
            if (count >= 2) {
                bDone = false
            }
        } while (bDone)
        return retStr.toString()
    }
}