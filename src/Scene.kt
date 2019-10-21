class Material(var color: Color = Color(), var reflection: Float = 0f)

class Model(var shape: Shape, var material: Material)

class Light(var position: Vec3, var color: Color)

class Scene {
    var background: Color = Color()
    var models = mutableSetOf<Model>()
    var lights = mutableSetOf<Light>()
}

fun Scene.model(shape: Shape, material: Material) {
    models.add(Model(shape, material))
}

fun Scene.light(position: Vec3, color: Color) {
    lights.add(Light(position, color))
}
