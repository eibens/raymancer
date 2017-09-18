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

        // Collect diffuse light by summing up contributions from each light source
        val diffuseLight = scene.lights.map { light ->
            val lightVector = light.position - hit
            val lightFalloff = 1 / lightVector.lengthSquared
            val attenuation = normal dot lightVector.normalize()
            return@map lightFalloff * attenuation * light.color * model.material.color
        }.reduce { totalLighting, lighting -> totalLighting + lighting }

        // Terminate the recursion
        val r = model.material.reflection
        if (depth == 0 || r <= 0f) return diffuseLight

        // Trace reflection ray and mix it with the diffuse light
        val reflected = ray.direction.reflect(normal)
        val reflectedLight = trace(Ray(hit, reflected), scene, depth - 1)
        return r * reflectedLight + (1 - r) * diffuseLight
    }
}
