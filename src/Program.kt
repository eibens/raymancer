import javafx.application.Application
import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.stage.Stage
import javafx.scene.image.Image as FxImage

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

class Main : Application() {
    override fun start(stage: Stage?) {
        val reflective = Material(reflection = 0.4f)
        val white = Material(Color(1f, 1f, 1f), 0.1f)
        val red = Material(Color(1f, 0f, 0f), 0.1f)
        val green = Material(Color(0f, 1f, 0f), 0.1f)

        val wall = 5f
        val minX = Plane(Vec3(-wall, 0f, 0f), Vec3(+1f, 0f, 0f))
        val maxX = Plane(Vec3(+wall, 0f, 0f), Vec3(-1f, 0f, 0f))
        val minY = Plane(Vec3(0f, -wall, 0f), Vec3(0f, +1f, 0f))
        val maxY = Plane(Vec3(0f, +wall, 0f), Vec3(0f, -1f, 0f))
        val minZ = Plane(Vec3(0f, 0f, -wall), Vec3(0f, 0f, +1f))
        val maxZ = Plane(Vec3(0f, 0f, +wall), Vec3(0f, 0f, -1f))
        val sphere1 = Sphere(Vec3(2f, 0f, 0f), 0.6f)
        val sphere2 = Sphere(Vec3(-2f, -1f, 0f), 0.6f)
        val sphere3 = Sphere(Vec3(0f, 2f, 2f), 2f)

        val scene = Scene().apply {
            light(Vec3(0f, -wall + 1f, 0f), Vec3(1f, 1f, 1f) * 20f)
            light(Vec3(-wall + 1f, wall - 1f, wall - 1f), Vec3(1f, 1f, 0.5f) * 5f)
            light(Vec3(wall - 1f, wall - 1f, wall - 1f), Vec3(0.5f, 0.8f, 1.0f) * 5f)
            model(sphere1, reflective)
            model(sphere2, reflective)
            model(sphere3, reflective)
            model(minX, red)
            model(maxX, green)
            model(minY, white)
            model(maxY, white)
            model(minZ, white)
            model(maxZ, white)
        }

        val image = Image(500, 500)
        Camera(-4f, 1f).shoot(image) {
            Raymancer.trace(it, scene, 10)
        }
        image.show(stage!!)
    }
}

fun Image.show(stage: Stage) {
    val bytesPerPixel = 3
    val stride = width * bytesPerPixel
    val data = toRgbArray()
    val output = WritableImage(width, height)
    output.pixelWriter.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), data, 0, stride)
    stage.apply {
        title = "Raymancer"
        scene = javafx.scene.Scene(Group(ImageView(output)))
        sizeToScene()
        show()
    }
}
