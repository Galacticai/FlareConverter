package com.galacticai.flareconverter.models

class SynonyMap<T> {
    constructor(vararg groups: Iterable<T>) : this(groups.toList())
    constructor(groups: Iterable<Iterable<T>>) {
        for (group in groups) add(group)
    }

    private val _groups: MutableList<Set<T>> = mutableListOf()
    val groups get() = _groups.toList()

    val size get() = _groups.flatten().size

    fun groupIndexOf(key: T) = groups.indexOfFirst {
        key in it
    }

    fun groupIndexOf(keys: Iterable<T>) = groups.indexOfFirst { group ->
        keys.any { it in group }
    }

    fun contains(key: T) = groupIndexOf(key) >= 0

    operator fun get(key: T): Set<T> {
        val i = groupIndexOf(key)
        return if (i >= 0) groups[i] else emptySet()
    }

    /**
     * add [Set] of [synonyms]
     * @return `true` if modified
     */
    fun add(synonyms: Iterable<T>): Boolean {
        val i = groupIndexOf(synonyms)
        if (i < 0) {
            _groups.add(synonyms.toSet())
            return true
        }
        val sizeBefore = _groups[i].size
        _groups[i] = _groups[i] + synonyms
        val sizeAfter = _groups[i].size
        return sizeBefore != sizeAfter
    }

    fun add(vararg synonyms: T) = add(synonyms.toSet())

    /**
     * remove [synonym]
     * @return `true` if modified
     */
    fun remove(synonym: T): Boolean {
        val i = groupIndexOf(synonym)
        if (i < 0) return false
        if (_groups[i].size == 1) _groups.removeAt(i)
        else _groups[i] = _groups[i] - synonym
        return true
    }
}
