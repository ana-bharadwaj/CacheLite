package cachetype.impl

import cachetype.GenericCache
import java.util.WeakHashMap

class WeakCache<K, V> : GenericCache<K, V> {

    private val map: MutableMap<K, V> = WeakHashMap()

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

    override fun toString(): String =
        map.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}:${it.value}" }
}
