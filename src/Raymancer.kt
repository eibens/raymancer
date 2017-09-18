object Raymancer {
    fun trace(ray: Ray, scene: Scene, depth: Int): Color {
        if (depth == 0) return Color()

        // Intersect ray with all objects and return closest positive hit.
        val (model, t) = intersect(ray, scene) ?: return scene.background
        val hit = ray(t - 0.001f)
        val normal = model.shape.computeNormal(hit)

        // Collect diffuse light by summing up contributions from each light source
        val diffuseLight = scene.lights.map { light ->
            val shadowRay = hit rayTo light.position
            val shadowHit = intersect(shadowRay, scene)?.second ?: Float.POSITIVE_INFINITY
            if (shadowHit < 1.0) return@map Color() // Shadow ray hits object before light source
            val lightFalloff = 1 / shadowRay.direction.lengthSquared
            val attenuation = normal dot shadowRay.direction.normalize()
            return@map lightFalloff * attenuation * light.color * model.material.color
        }.reduce { totalLighting, lighting -> totalLighting + lighting }

        // Trace reflection ray if the material is reflective
        val r = model.material.reflection
        val reflectedLight = if (r > 0f) {
            // Trace reflection ray and mix it with the diffuse light
            val reflected = ray.direction.reflect(normal)
            trace(Ray(hit, reflected), scene, depth - 1)
        } else Color()

        // Mix diffuse and reflected light
        return r * reflectedLight + (1 - r) * diffuseLight
    }

    private fun intersect(ray: Ray, scene: Scene): Pair<Model, Float>? {
        return scene.models
                .flatMap { model -> (model.shape intersect ray).map { model to it } }
                .filter { (_, t) -> t > 0f }
                .minBy { (_, t) -> t }
    }
}
