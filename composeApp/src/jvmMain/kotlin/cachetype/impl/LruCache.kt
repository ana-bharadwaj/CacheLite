package cachetype.impl

import cachetype.GenericCache

class LruCache<K, V>(
    private val capacity: Int
) : GenericCache<K, V> {

    private val map: MutableMap<K, V> =
        object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
                return size > capacity
            }
        }

    override fun get(key: K): V? = map[key]

    override fun set(key: K, value: V) {
        map[key] = value
    }

    override fun remove(key: K): V? = map.remove(key)

    override fun clear() {
        map.clear()
    }

    override val size: Int
        get() = map.size

    override fun entries(): List<Pair<K, V>> =
        map.entries.map { it.key to it.value }

    override fun toString(): String {
        // LinkedHashMap(accessOrder = true) keeps LRU → MRU order
        return map.entries.joinToString(prefix = "[LRU:", postfix = "]") { "${it.key}:${it.value}" }
    }
}
