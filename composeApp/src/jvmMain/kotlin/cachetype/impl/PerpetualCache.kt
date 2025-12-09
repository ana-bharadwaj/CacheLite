package cachetype.impl

import cachetype.GenericCache

class PerpetualCache<K, V> : GenericCache<K, V> {

    private val map = mutableMapOf<K, V>()

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
        return map.entries.joinToString(prefix = "{", postfix = "}") {
            "${it.key}:${it.value}"
        }
    }
}
