import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.EventListener
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
    window.addEventListener("load", EventListener { onLoad() })
}

fun onLoad() {
    setupLogo()
}

fun setupLogo() {
    PixelatedLogo().setup(document.getElementById("logo") as HTMLCanvasElement, "img/adriantodt.png.txt")
}
