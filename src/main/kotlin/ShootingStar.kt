data class ShootingStar(
    val color: Color
) {
    var lifetime: Int = 0

    val starEntropy = color.red + color.green + color.blue
    val starSize = (color.blue + color.green) / 128 + 1
    val starSpeed = ((color.red * 4 + color.green * 2 + color.blue) / 256 + 1)
}