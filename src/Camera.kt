data class Camera(val offset: Float, val focalDistance: Float) {
    fun shoot(image: Image, trace: (Ray) -> Color) {
        if (image.width != image.height) throw NotImplementedError("Camera does not support non-square images.")
        val resolution = image.width
        val cameraPos = Vec3(0f, 0f, offset)
        image.iterate { i, j, _ ->
            val x = (i.toFloat() / resolution) * 2 - 1f
            val y = (j.toFloat() / resolution) * 2 - 1f
            val pos = Vec3(x, y, offset + focalDistance)
            val ray = cameraPos rayTo pos
            image[i, j] = trace(ray)
        }
    }
}
