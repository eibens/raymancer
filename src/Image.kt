import java.nio.ByteBuffer

typealias Color = Vec3

class Image(val width: Int, val height: Int) {
    private val data = Array(width * height, { Color() })

    private fun getIndex(x: Int, y: Int): Int {
        if (x < 0 || width <= x) throw IllegalArgumentException("X is out of range.")
        if (y < 0 || height <= y) throw IllegalArgumentException("Y is out of range.")
        return y * width + x
    }

    operator fun get(x: Int, y: Int) = data[getIndex(x, y)]

    operator fun set(x: Int, y: Int, color: Color) {
        data[getIndex(x, y)] = color
    }

    fun iterate(action: (Int, Int, Color) -> Unit) {
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                action(x, y, this[x, y])
            }
        }
    }

    fun toRgbArray(): ByteArray {
        val buffer = ByteBuffer.allocate(width * height * 3)
        iterate { _, _, (r, g, b) ->
            buffer.apply {
                put(toByte(r))
                put(toByte(g))
                put(toByte(b))
            }
        }
        return buffer.array()
    }

    private fun toByte(value: Float): Byte {
        val discrete = value.coerceIn(0f, 1f) * 255
        if (discrete <= 127) return discrete.toByte()
        return (discrete - 256).toByte()
    }
}
