/// Vector with three float components.
data class Vec3(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f)

/// Value indicating whether all components of this vector are zero.
val Vec3.isZero: Boolean
    get() = x == 0f && y == 0f && z == 0f

/// Adds two vectors component-wise.
operator fun Vec3.plus(v: Vec3) = Vec3(x + v.x, y + v.y, z + v.z)

/// Negates this vector.
operator fun Vec3.unaryMinus() = Vec3(-x, -y, -z)

/// Subtracts two vectors component-wise.
operator fun Vec3.minus(v: Vec3) = this + (-v)

/// Multiplies two vectors component-wise.
operator fun Vec3.times(v: Vec3) = Vec3(x * v.x, y * v.y, z * v.z)

/// Scales this vector by a value.
operator fun Vec3.times(s: Float) = Vec3(s * x, s * y, s * z)

/// Scales a vector by this value.
operator fun Float.times(v: Vec3) = v * this

/// Scales this vector by the reciprocal of a value.
operator fun Vec3.div(s: Float) = Vec3(x / s, y / s, z / s)

/// Sum of the components of this vector.
inline val Vec3.innerSum: Float
    get() = x + y + z

/// Computes the dot product of two vectors.
infix fun Vec3.dot(v: Vec3) = (this * v).innerSum

/// Reflects this direction on the specified normal vector.
fun Vec3.reflect(normal: Vec3) =
        this - 2 * (this dot normal) * normal

/// Squared euclidean norm of this vector.
val Vec3.lengthSquared: Float
    get() = this dot this

/// Euclidean norm of this vector.
val Vec3.length: Float
    get() = Math.sqrt(lengthSquared.toDouble()).toFloat()

/// Scales this vector to length one.
/// If this vector is zero, the zero vector will be returned.
fun Vec3.normalize() = if (isZero) this else this / length

/// Creates a ray that starts at this position and goes through the specified position.
infix fun Vec3.rayTo(point: Vec3) = Ray(this, point - this)

/// Parametrized ray in 3D space, defined by its origin and direction.
data class Ray(val origin: Vec3, val direction: Vec3)

/// Computes the point along this ray for a parameter.
operator fun Ray.invoke(t: Float) = origin + t * direction

/// Changes the origin of this ray to a point along itself.
fun Ray.move(t: Float) = Ray(this(t), direction)

/// Plane in 3D space, defined by its origin and surface normal.
data class Plane(val origin: Vec3, val normal: Vec3)

/// Computes the ray parameters for the intersection of a ray and this plane.
infix fun Plane.intersect(ray: Ray): Float? {
    val dDotN = normal dot ray.direction
    if (dDotN == 0f) return null
    return ((origin - ray.origin) dot normal) / dDotN
}

/// Sphere in 3D space, defined by its center and radius.
data class Sphere(val center: Vec3, val radius: Float)

/// Computes the normalized direction of a ray from the sphere center to the specified point.
fun Sphere.computeNormal(point: Vec3) =
        (point - center).normalize()

/// Computes the ray parameters for the intersection of a ray and this sphere.
infix fun Sphere.intersect(ray: Ray): List<Float> {
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
