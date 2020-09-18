import kotlin.random.Random

data class FallingPixel(val x: Int, val y: Int, val color: Color) : Comparable<FallingPixel> {
    val speed: Int = Random.nextInt(5, 10)
    var lifetime: Int = -Random.nextInt(40, 60)

    override fun compareTo(other: FallingPixel): Int {
        return naturalOrder.compare(this, other)
    }

    companion object {
        val naturalOrder = compareBy(FallingPixel::y).thenBy(FallingPixel::x)
    }
}