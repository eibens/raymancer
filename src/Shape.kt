/// Shape in 3D space, that can be intersected with a ray.
interface Shape {
    /// Computes the intersection points of this shape and the specified ray.
    infix fun intersect(ray: Ray): List<Float>

    /// Computes the surface normal closest to the specified position.
    infix fun computeNormal(point: Vec3): Vec3
}

/// Plane in 3D space, defined by its origin and surface normal.
data class Plane(val origin: Vec3, val normal: Vec3) : Shape {
    override fun computeNormal(point: Vec3) = normal

    override infix fun intersect(ray: Ray): List<Float> {
        val dDotN = normal dot ray.direction
        if (dDotN == 0f) return emptyList()
        return listOf(((origin - ray.origin) dot normal) / dDotN)
    }
}

/// Sphere in 3D space, defined by its center and radius.
data class Sphere(val center: Vec3, val radius: Float) : Shape {

    override fun computeNormal(point: Vec3) =
            (point - center).normalize()

    override infix fun intersect(ray: Ray): List<Float> {
        // Compute coefficients for ray-sphere intersection polynomial: at^2 + bt + c
        val origin = ray.origin - center
        val a = ray.direction.lengthSquared
        val b = 2 * (origin dot ray.direction)
        val c = origin.lengthSquared - radius * radius
        val discriminant = b * b - 4 * a * c

        // Compute the solution for t given the value for the root.
        val t = { root: Float -> -(b + root) / (2 * a) }

        // No intersection
        if (discriminant < 0) return emptyList()

        // One intersection
        if (discriminant == 0f) return listOf(t(0f))

        // Two intersections
        val root = Math.sqrt(discriminant.toDouble()).toFloat()
        return listOf(t(root), t(-root))
    }
}
