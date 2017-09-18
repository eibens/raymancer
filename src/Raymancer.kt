object Raymancer {
    fun trace(ray: Ray, scene: Scene, depth: Int): Color {
        // Intersect ray with all objects and return closest positive hit.
        val (model, t) = scene.models
                .flatMap { model -> (model.shape intersect ray).map { model to it } }
                .filter { (_, t) -> t > 0f }
                .minBy { (_, t) -> t }
                ?: return scene.background

        val hit = ray(t - 0.001f)
        val normal = model.shape.computeNormal(hit)

        if (depth > 0 && model.material.reflective) {
            val reflected = ray.direction.reflect(normal)
            return trace(Ray(hit, reflected), scene, depth - 1) * 0.5f
        }

        return scene.lights.map { light ->
            val lightVector = light.position - hit
            val lightFalloff = 1 / lightVector.lengthSquared
            val attenuation = normal dot lightVector.normalize()
            return@map lightFalloff * attenuation * light.color * model.material.color
        }.reduce { totalLighting, lighting -> totalLighting + lighting }
    }
}
