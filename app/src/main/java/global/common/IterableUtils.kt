package global.common

/** General purpose calculation for [Iterable] (can use any [calculator] function on any [Number] type) (similar to [Iterable.reduce] but for [Number]s)
 * @param selector function that maps each element to a [R] ([Number])
 * @param calculator performs a calculation on an [accumulator] (result of previous calculations) and an [element] that was mapped to [R] ([Number]) by [selector]
 * @return calculation result for all elements in the [Iterable] */
inline fun <T, reified R : Number> Iterable<T>.calculateBy(
    selector: (T) -> R,
    calculator: (accumulator: R, element: R) -> R
): R {
    var value: R = 0 as R
    for (element in this)
        value = calculator(value, selector(element))
    return value
}

fun <T, R> Iterable<T>.reduceBetter(initial: R, operation: (R, T) -> R): R {
    var accumulator: R = initial
    for (element in this)
        accumulator = operation(accumulator, element)
    return accumulator
}
