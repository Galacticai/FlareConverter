package global.common.models

class CircularList<T>(capacity: Int) : MutableList<T> {
    private val items: MutableList<T> = mutableListOf()

    var capacity: Int = capacity
        set(value) {
            if (capacity < 1)
                throw IllegalArgumentException("Capacity must be at least 1")
            field = value
        }


    override val size: Int get() = items.size
    val sizeAvailable: Int get() = capacity - size

    override fun clear() = items.clear()
    override fun get(index: Int): T = items[index]
    override fun set(index: Int, element: T): T {
        if (index >= capacity) throw indexOutOfBoundsException(index)
        return items.set(index, element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (elements.size > capacity) return false
        for (element in elements) add(element)
        return true
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (index < 0 || index >= capacity) throw indexOutOfBoundsException(index)

        //? Overflow
        //? [~ ~ ~ O O] X X...
        if (index + elements.size > capacity) return false

        val available = capacity - size
        //? Not enough space
        //? [O O O O O] X X...
        if (elements.size > available) return false

        items.addAll(index, elements)
        return true
    }

    override fun add(index: Int, element: T) {
        add(index, element, force = false)
    }

    fun add(index: Int, element: T, force: Boolean): Boolean {
        if (index >= capacity) throw indexOutOfBoundsException(index)
        if (sizeAvailable < 1)
            if (force) removeAt(index)
            else return false
        items[index] = element
        return true
    }

    /** Add an [element]
     * @return true if capacity was reached and the oldest item was removed in favor for the new item
     */
    override fun add(element: T): Boolean {
        var looped = false
        if (items.size >= capacity) {
            looped = true
            removeAt(0)
        }
        items.add(element)
        return looped
    }

    override fun isEmpty(): Boolean = items.isEmpty()
    override fun iterator(): MutableIterator<T> = items.iterator()
    override fun listIterator(): MutableListIterator<T> = items.listIterator()
    override fun listIterator(index: Int): MutableListIterator<T> = items.listIterator(index)
    override fun removeAll(elements: Collection<T>): Boolean = items.removeAll(elements)
    override fun remove(element: T): Boolean = items.remove(element)
    override fun lastIndexOf(element: T): Int = items.lastIndexOf(element)
    override fun indexOf(element: T): Int = items.indexOf(element)
    override fun containsAll(elements: Collection<T>): Boolean = items.containsAll(elements)
    override fun contains(element: T): Boolean = items.contains(element)
    override fun removeAt(index: Int) = items.removeAt(index)
    override fun retainAll(elements: Collection<T>): Boolean = items.retainAll(elements)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        items.subList(fromIndex, toIndex)

    private fun indexOutOfBoundsException(index: Int) =
        IndexOutOfBoundsException("Index ($index) exceeds the allowed capacity for this list ($capacity)")

}
