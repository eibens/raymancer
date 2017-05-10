import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
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
        val bytesPerPixel = 3
        val width = 500
        val height = 500
        val stride = bytesPerPixel * width

        val image = Image(width, height)
        raymancer(image)
        val data = image.toRgbArray()

        val output = WritableImage(width, height)
        output.pixelWriter.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), data, 0, stride)
        stage!!.apply {
            title = "Raymancer"
            scene = Scene(Group(ImageView(output)))
            sizeToScene()
            show()
        }
    }
}
