import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.xhr.XMLHttpRequest
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class PixelatedLogo : ShootingStarsJS() {
    private val pixelsRead = ArrayList<FallingPixel>()
    private val pixelsFalling = ArrayList<FallingPixel>()
    private val renderedCanvas = document.createElement("canvas") as HTMLCanvasElement
    private val renderedCtx = renderedCanvas.getContext("2d") as CanvasRenderingContext2D
    private var logoSize: Dimension? = null

    fun setup(canvas: HTMLCanvasElement, logoLocation: String) {
        super.setup(canvas)

        XMLHttpRequest().apply {
            open("GET", logoLocation)
            onreadystatechange = {
                if (readyState.toInt() == 4) {
                    if (status.toInt() == 200) {
                        parseResponse(responseText)
                    } else {
                        console.log("HTTP Error", status, statusText)
                    }
                }
            }
        }.send()
    }

    override fun loop(canvas: HTMLCanvasElement) {
        super.loop(canvas)
        if (pixelsRead.isNotEmpty()) {
            val pixelsTaken = pixelsRead.take(Random.nextInt(10, 15))
            pixelsRead.removeAll(pixelsTaken)
            pixelsFalling.addAll(pixelsTaken)
        }
        if (pixelsFalling.isNotEmpty()) {
            val (notFalling, stillFalling) = pixelsFalling.partition { it.lifetime >= 0 }
            pixelsFalling.retainAll(stillFalling)
            for (pixel in notFalling) {
                renderedCtx.fillStyle = pixel.color.toString()
                renderedCtx.fillRect(pixel.x.toDouble(), pixel.y.toDouble(), 1.0, 1.0)
            }
        }
    }

    override fun draw(ctx: CanvasRenderingContext2D) {
        super.draw(ctx)
        ctx.imageSmoothingEnabled = false
        logoSize?.let { ctx.renderLogo(it) }
    }

    fun CanvasRenderingContext2D.renderLogo(d: Dimension) {
        val h = canvas.height
        val w = canvas.width
        val scale = ceil(min(h.toDouble() * 0.5 / d.height, w.toDouble() * 0.5 / d.width))
        val baseX = (w - d.width * scale) / 2
        val baseY = (h - d.height * scale) / 2

        for (p in pixelsFalling) {
            val color = p.color
            val n = p.lifetime++
            val speed = p.speed
            val goalX = baseX + p.x.toDouble() * scale
            val goalY = baseY + p.y.toDouble() * scale
            val curX = (goalX + n * speed).roundToInt()
            val curY = (goalY + n * speed).roundToInt()
            renderShootingStar(curX, curY, color, speed, scale.toInt())
        }

        drawImage(renderedCanvas, baseX, baseY, renderedCanvas.width * scale, renderedCanvas.height * scale)
    }

    fun parseResponse(responseText: String) {
        val (widthText, heightText, b64map) = responseText.split(',')
        val width = widthText.toInt()
        val height = heightText.toInt()
        renderedCanvas.width = width
        renderedCanvas.height = height
        logoSize = Dimension(height, width)
        val parsed = window.atob(b64map)
            .map { it.toInt().toString(16).padStart(2, '0') }
            .chunked(3)
            .mapIndexed { i, it ->
                FallingPixel(
                    i % width,
                    i / width,
                    Color.decode(it.joinToString(""))
                )
            }
            .filter { it.color != Color.black }
            .groupBy { it.y / 1.7 }
            .flatMap { it.value.shuffled() }
            .reversed()
        pixelsRead += parsed
    }
}