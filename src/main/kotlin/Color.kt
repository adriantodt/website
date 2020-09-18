import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Color private constructor(val rgb : Int) {
    val red: Int = rgb shr 16 and 0xFF
    val green: Int = rgb shr 8 and 0xFF
    val blue: Int = rgb shr 0 and 0xFF
    val alpha: Int = rgb shr 24 and 0xff
    val hex = "#" + red.toString(16).padStart(2, '0') +
            green.toString(16).padStart(2, '0') +
            blue.toString(16).padStart(2, '0')

    fun brighter(): Color {
        var r = red
        var g = green
        var b = blue
        val alpha = alpha
        val i = (1.0 / (1.0 - FACTOR)).toInt()
        if (r == 0 && g == 0 && b == 0) {
            return of(i, i, i, alpha)
        }
        if (r in 1 until i) r = i
        if (g in 1 until i) g = i
        if (b in 1 until i) b = i
        return of(
            min((r / FACTOR).toInt(), 255),
            min((g / FACTOR).toInt(), 255),
            min((b / FACTOR).toInt(), 255),
            alpha
        )
    }

    fun darker(): Color {
        return of(
            max((red * FACTOR).toInt(), 0),
            max((green * FACTOR).toInt(), 0),
            max((blue * FACTOR).toInt(), 0),
            alpha
        )
    }

    override fun toString(): String {
        return hex
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Color) return false
        return rgb == other.rgb
    }

    override fun hashCode(): Int {
        return rgb.hashCode()
    }

    companion object {
        private val values = LinkedHashMap<Int, Color>()

        fun of(r: Int, g: Int, b: Int, a: Int = 255): Color {
            val rgb = a and 0xFF shl 24 or (r and 0xFF shl 16) or (g and 0xFF shl 8) or (b and 0xFF shl 0)
            return values.getOrPut(rgb) {
                testColorValueRange(r, g, b, a)
                Color(rgb)
            }
        }

        fun of(rgb: Int): Color {
            return values.getOrPut(rgb) { Color(rgb) }
        }

        val white = of(255, 255, 255)
        val lightGray = of(192, 192, 192)
        val gray = of(128, 128, 128)
        val darkGray = of(64, 64, 64)
        val black = of(0, 0, 0)
        val red = of(255, 0, 0)
        val pink = of(255, 175, 175)
        val orange = of(255, 200, 0)
        val yellow = of(255, 255, 0)
        val green = of(0, 255, 0)
        val magenta = of(255, 0, 255)
        val cyan = of(0, 255, 255)
        val blue = of(0, 0, 255)

        private fun testColorValueRange(r: Int, g: Int, b: Int, a: Int) {
            var rangeError = false
            var badComponentString = ""
            if (a < 0 || a > 255) {
                rangeError = true
                badComponentString = "$badComponentString Alpha"
            }
            if (r < 0 || r > 255) {
                rangeError = true
                badComponentString = "$badComponentString Red"
            }
            if (g < 0 || g > 255) {
                rangeError = true
                badComponentString = "$badComponentString Green"
            }
            if (b < 0 || b > 255) {
                rangeError = true
                badComponentString = "$badComponentString Blue"
            }
            require(!rangeError) { "Color parameter outside of expected range:$badComponentString" }
        }

        private const val FACTOR = 0.7

        fun decode(s: String): Color {
            val i = s.toInt(16)
            return of(i shr 16 and 0xFF, i shr 8 and 0xFF, i and 0xFF)
        }

        fun getHSBColor(hue: Float, saturation: Float, brightness: Float): Color {
            var r = 0
            var g = 0
            var b = 0
            if (saturation == 0f) {
                b = (brightness * 255.0f + 0.5f).toInt()
                g = b
                r = g
            } else {
                val h = (hue - floor(hue)) * 6.0f
                val f = h - floor(h)
                val p = brightness * (1.0f - saturation)
                val q = brightness * (1.0f - saturation * f)
                val t = brightness * (1.0f - saturation * (1.0f - f))
                when (h.toInt()) {
                    0 -> {
                        r = (brightness * 255.0f + 0.5f).toInt()
                        g = (t * 255.0f + 0.5f).toInt()
                        b = (p * 255.0f + 0.5f).toInt()
                    }
                    1 -> {
                        r = (q * 255.0f + 0.5f).toInt()
                        g = (brightness * 255.0f + 0.5f).toInt()
                        b = (p * 255.0f + 0.5f).toInt()
                    }
                    2 -> {
                        r = (p * 255.0f + 0.5f).toInt()
                        g = (brightness * 255.0f + 0.5f).toInt()
                        b = (t * 255.0f + 0.5f).toInt()
                    }
                    3 -> {
                        r = (p * 255.0f + 0.5f).toInt()
                        g = (q * 255.0f + 0.5f).toInt()
                        b = (brightness * 255.0f + 0.5f).toInt()
                    }
                    4 -> {
                        r = (t * 255.0f + 0.5f).toInt()
                        g = (p * 255.0f + 0.5f).toInt()
                        b = (brightness * 255.0f + 0.5f).toInt()
                    }
                    5 -> {
                        r = (brightness * 255.0f + 0.5f).toInt()
                        g = (p * 255.0f + 0.5f).toInt()
                        b = (q * 255.0f + 0.5f).toInt()
                    }
                }
            }
            return of(-0x1000000 or (r shl 16) or (g shl 8) or (b shl 0))
        }
    }
}