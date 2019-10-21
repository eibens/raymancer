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

/// Computes the refracted direction of this vector using Snell's law.
fun Vec3.refract(normal: Vec3, ior: Float): Vec3 {
    val c1 = -this dot normal
    val eta = if (c1 < 0) ior else 1f / ior
    val c2 = 1 - eta * eta * (1 - c1 * c1)
    if (c2 < 0f) return Vec3() // total internal reflection
    val c2root = Math.sqrt(c2.toDouble()).toFloat()
    return eta * this + (eta * c1 - c2root) * normal
}

/// Angle between this vector and the specified vector.
infix fun Vec3.angleTo(v: Vec3) =
        Math.acos((this dot v).toDouble())

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
