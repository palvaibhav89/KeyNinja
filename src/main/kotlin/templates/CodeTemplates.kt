package templates

object CodeTemplates {

    const val CORE_LOGIC = """package %s

import %s.view.PKeys
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import kotlin.math.pow

open class AppCompatViewBase {

    companion object {
        private const val RB = -1
        private const val EB = 4
        private val SB = charArrayOf(
            65.toChar(), 66.toChar(), 67.toChar(), 68.toChar(), 69.toChar(), 70.toChar(), 71.toChar(), 72.toChar(), 73.toChar(), 74.toChar(), 75.toChar(), 76.toChar(), 77.toChar(), 78.toChar(), 79.toChar(), 80.toChar(), 81.toChar(), 82.toChar(), 83.toChar(), 84.toChar(), 85.toChar(), 86.toChar(), 87.toChar(), 88.toChar(), 89.toChar(), 90.toChar(), 97.toChar(), 98.toChar(), 99.toChar(), 100.toChar(), 101.toChar(), 102.toChar(), 103.toChar(), 104.toChar(), 105.toChar(), 106.toChar(), 107.toChar(), 108.toChar(), 109.toChar(), 110.toChar(), 111.toChar(), 112.toChar(), 113.toChar(), 114.toChar(), 115.toChar(), 116.toChar(), 117.toChar(), 118.toChar(), 119.toChar(), 120.toChar(), 121.toChar(), 122.toChar(), 48.toChar(), 49.toChar(), 50.toChar(), 51.toChar(), 52.toChar(), 53.toChar(), 54.toChar(), 55.toChar(), 56.toChar(), 57.toChar(), 43.toChar(), 47.toChar()
        )
        private val i = IntArray(128)
    }

    private val b = intArrayOf(
        %s
    )

    init {
        try {
            i()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    @Throws(JSONException::class, NoSuchFieldException::class, IllegalAccessException::class)
    private fun i() {
        for (i in SB.indices) AppCompatViewBase.i[SB[i].code] = i
        parseInfo(JSONObject(pv(gB())))
    }

    private fun d(s: String): ByteArray {
        val d = if (s.endsWith("==")) 2 else if (s.endsWith("=")) 1 else 0
        val bf = ByteArray(s.length * 3 / 4 - d)
        val m = 0xFF
        var index = 0
        run {
            var i = 0
            while (i < s.length) {
                val c0 = AppCompatViewBase.i[s[i].code]
                val c1 = AppCompatViewBase.i[s[i + 1].code]
                bf[index++] = (c0 shl 2 or (c1 shr 4) and m).toByte()
                if (index >= bf.size) {
                    return bf
                }
                val c2 = AppCompatViewBase.i[s[i + 2].code]
                bf[index++] = (c1 shl 4 or (c2 shr 2) and m).toByte()
                if (index >= bf.size) {
                    return bf
                }
                val c3 = AppCompatViewBase.i[s[i + 3].code]
                bf[index++] = (c2 shl 6 or c3 and m).toByte()
                i += 4
            }
        }
        return bf
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun parseInfo(jO: JSONObject) {
        val it = jO.keys()
        while (it.hasNext()) {
            val k = it.next()
            val v = jO.optString(k)
            sv(k, pv(v))
        }
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun sv(k: String, v: String) {
        val t = PKeys::class.java.getDeclaredField(k)
        t.isAccessible = true
        t[null] = v
    }

    private fun pv(b: String): String {
        val pb = RB + 1
        val n = Integer.valueOf(b[pb].toString())
        return String(d(b.substring(pb + 1, n + 1) + b.substring(n + 2)), StandardCharsets.UTF_8)
    }

    private fun gB(): String {
        var s = ""
        for (b in b) {
            var p = RB * EB * b * 16
            p = (p * RB / (2.0.pow(EB.toDouble()) * EB)).toInt()
            s += p.toChar().toString()
        }
        return s
    }
}
    """

    const val STATIC_KEYS_FILE = """package %s;

object PKeys {
    %s
}
    """

    const val INIT_HELPER_FILE = """package %s

object AppCompatViewHelper : AppCompatViewBase() {
    fun init() {
    }
}
    """
}