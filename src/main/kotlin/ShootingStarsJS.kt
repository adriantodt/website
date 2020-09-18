import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.roundToInt
import kotlin.random.Random

open class ShootingStarsJS {
    private val stars = ArrayList<ShootingStar>()

    fun setup(canvas: HTMLCanvasElement) {
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        val bufferCanvas = document.createElement("canvas") as HTMLCanvasElement
        val bufferCtx = bufferCanvas.getContext("2d") as CanvasRenderingContext2D
        window.setInterval({
            // logic loop
            loop(canvas)

            // setup buffer canvas
            canvas.width = canvas.clientWidth
            canvas.height = canvas.clientHeight
            bufferCanvas.width = canvas.width / 10 * 10
            bufferCanvas.height = canvas.height / 10 * 10

            // clear canvas
            bufferCtx.clearRect(0.0, 0.0, bufferCanvas.width.toDouble(), bufferCanvas.height.toDouble())

            // draw loop
            draw(bufferCtx)

            // clear canvas
            ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

            // draw
            ctx.drawImage(
                bufferCanvas,
                (canvas.width - bufferCanvas.width) / 2.0,
                (canvas.height - bufferCanvas.height) / 2.0
            )
        }, 25)
    }

    open fun loop(canvas: HTMLCanvasElement) {
        stars.add(ShootingStar(Color.getHSBColor(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())))
    }

    open fun draw(ctx: CanvasRenderingContext2D) {
        ctx.renderStars()
    }

    private inline fun CanvasRenderingContext2D.renderStars() {
        val h = canvas.height
        val w = canvas.width
        val hw = h + w
        val ratio = hw / (255 * 3f)
        stars.forEach {
            val color = it.color
            val n = it.lifetime++

            val entropy = (it.starEntropy * ratio).roundToInt()

            val spawnX: Int
            val spawnY: Int

            if (entropy < h) {
                spawnX = 0
                spawnY = h - entropy
            } else {
                spawnX = entropy - h
                spawnY = 0
            }

            val speed = it.starSpeed

            val curX = spawnX + n * speed
            val curY = spawnY + n * speed

            val size = it.starSize

            if (curX > w + size && curY > h + size) {
                stars.remove(it)
            } else {
                renderShootingStar(curX, curY, color, speed, size)
            }
        }
    }

    inline fun CanvasRenderingContext2D.renderShootingStar(
        curX: Int,
        curY: Int,
        color: Color,
        speed: Int,
        size: Int
    ) {
        generateSequence(color, Color::darker).take(size * 2).forEachIndexed { i, c ->
            fillStyle = c.toString()
            fillRect(
                (curX - i * speed).toDouble(),
                (curY - i * speed).toDouble(),
                (size - i / 2).toDouble(),
                (size - i / 2).toDouble()
            )
        }
    }
}