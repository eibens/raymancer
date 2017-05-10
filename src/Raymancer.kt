
class Material(var color: Color = Color(), val reflective: Boolean = false)
class Model(val shape: Any, val material: Material)
class Light(val position: Vec3, val color: Color)
class Scene {
    val models = mutableSetOf<Model>()
    val lights = mutableSetOf<Light>()
}

fun buildScene(): Scene {
    val reflective = Material(reflective = true)
    val white = Material(Color(1f, 1f, 1f))
    val red = Material(Color(1f, 0f, 0f))
    val blue = Material(Color(0f, 0f, 1f))
    val green = Material(Color(0f, 1f, 0f))
    val yellow = Material(Color(1f, 1f, 0f))

    val wall = 5f
    val minX = Plane(Vec3(-wall, 0f, 0f), Vec3(+1f, 0f, 0f))
    val maxX = Plane(Vec3(+wall, 0f, 0f), Vec3(-1f, 0f, 0f))
    val minY = Plane(Vec3(0f, -wall, 0f), Vec3(0f, +1f, 0f))
    val maxY = Plane(Vec3(0f, +wall, 0f), Vec3(0f, -1f, 0f))
    val minZ = Plane(Vec3(0f, 0f, -wall), Vec3(0f, 0f, +1f))
    val maxZ = Plane(Vec3(0f, 0f, +wall), Vec3(0f, 0f, -1f))
    val sphere1 = Sphere(Vec3(2f, 0f, 0f), 0.6f)
    val sphere2 = Sphere(Vec3(-2f, -1f, 0f), 0.6f)
    val sphere3 = Sphere(Vec3(0f, 2f, 4f), 3f)
    val light = Light(Vec3(0f, -wall + 1f, 0f), Vec3(1f, 1f, 1f) * 20f)

    return Scene().apply {
        lights.add(light)
        models.apply {
            add(Model(sphere1, reflective))
            add(Model(sphere2, reflective))
            add(Model(sphere3, reflective))
            add(Model(minX, reflective))
            add(Model(maxX, green))
            add(Model(minY, white))
            add(Model(maxY, yellow))
            add(Model(minZ, reflective))
            add(Model(maxZ, blue))
        }
    }
}

fun raymancer(output: Image) {
    if (output.width != output.height)
        throw IllegalArgumentException("Output image must be square.")

    val cameraOffset = -4f
    val focalDistance = 1f

    val width = output.width
    val cameraPos = Vec3(0f, 0f, cameraOffset)
    output.iterate { i, j, _ ->
        val x = (i.toFloat() / width) * 2 - 1f
        val y = (j.toFloat() / width) * 2 - 1f
        val pos = Vec3(x, y, cameraOffset + focalDistance)
        val ray = cameraPos rayTo pos
        output[i, j] = trace(ray, buildScene(), 10)
    }
}

private fun trace(ray: Ray, scene: Scene, depth: Int): Color {
    val background = Color()
    val (model, t) = scene.models
            .map { model -> model to intersect(ray, model.shape) }
            .filter { (_, t) -> t != null } // remove non-intersections
            .map { (model, t) -> model to t!! } // cast t to non-nullable
            .filter { (_, t) -> t > 0f } // remove intersections behind the ray origin
            .minBy { (_, t) -> t } // find the closest intersection
            ?: return background

    val position = ray(t - 0.001f)
    val normal = computeNormal(position, model.shape)

    if (depth > 0 && model.material.reflective) {
        val reflected = ray.direction.reflect(normal)
        return trace(Ray(position, reflected), scene, depth - 1) * 0.5f
    }

    return scene.lights.map { light ->
        val lightVector = light.position - position
        val lightFalloff = 1 / lightVector.lengthSquared
        val attenuation = normal dot lightVector.normalize()
        return@map lightFalloff * attenuation * light.color * model.material.color
    }.reduce { totalLighting, lighting -> totalLighting + lighting }
}

private fun intersect(ray: Ray, shape: Any): Float? {
    return when (shape) {
        is Sphere -> shape.intersect(ray).filter { t -> t > 0 }.min()
        is Plane -> shape.intersect(ray)
        else -> throw RuntimeException("Unknown shape.")
    }
}

private fun computeNormal(point: Vec3, shape: Any): Vec3 {
    return when (shape) {
        is Sphere -> shape.computeNormal(point)
        is Plane -> shape.normal
        else -> throw RuntimeException("Unknown shape.")
    }
}
