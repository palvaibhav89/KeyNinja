package utils

import java.util.*
import kotlin.collections.ArrayList

object CryptoUtils {
    
    fun cypher(value: String): String {
        val bytes = value.toByteArray()
        val encoded = Base64.getEncoder().encodeToString(bytes)
        return corruptStr(encoded)
    }

    fun toInt(value: String): String {
        val ints = ArrayList<Int>()
        value.forEach { char ->
            ints.add(char.code)
        }
        return ints.joinToString()
    }

    private fun corruptStr(encValStr: String): String {
        var encVal = encValStr
        var l = encVal.length
        if (l >= 10) l = 9
        val num = (0..l).random()
        encVal = encVal.substring(0, num) + findChar(1) + encVal.substring(num)
        return appendIndexToInsert(encVal, num)
    }

    private fun appendIndexToInsert(encVal: String, num: Int): String {
        return num.toString() + encVal
    }

    fun findChar(l: Int): String {
        val randomChars = "abcdefghijklmnopqrstuvwxyz0123456789"
        val result = StringBuilder()
        for (i in 0 until l) {
            val randomNumber = (randomChars.indices).random()
            result.append(randomChars[randomNumber])
        }
        return result.toString()
    }
}