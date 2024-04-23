package global.common

inline fun <reified T : Number> numberToGeneric(f: Float): T {
    return when (T::class) {
        Float::class -> f as T
        Double::class -> f.toDouble() as T
        Byte::class -> f.toInt().toByte() as T
        Short::class -> f.toInt().toShort() as T
        Int::class -> f.toInt() as T
        Long::class -> f.toLong() as T
        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }
}