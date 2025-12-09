package cachetype.impl

import cachetype.GenericCache
import java.lang.ref.SoftReference

class SoftCache<K, V>(
    private val delegate: GenericCache<K, V>
) : GenericCache<K, V> {

    private val map = mutableMapOf<K, SoftReference<V>>()

    private fun cleanStaleEntries() {
        val it = map.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.value.get() == null) {
                it.remove()
            }
        }
    }

    override fun get(key: K): V? {
        cleanStaleEntries()
        val ref = map[key] ?: return null
        val value = ref.get()
        if (value == null) {
            map.remove(key)
        }
        return value
    }

    override fun set(key: K, value: V) {
        cleanStaleEntries()
        map[key] = SoftReference(value)
    }

    override fun remove(key: K): V? {
        cleanStaleEntries()
        val ref = map.remove(key) ?: return null
        return ref.get()
    }

    override fun clear() {
        map.clear()
        delegate.clear()
    }

    override val size: Int
        get() {
            cleanStaleEntries()
            return map.size
        }

    override fun entries(): List<Pair<K, V>> {
        cleanStaleEntries()
        return map.entries.mapNotNull { (k, ref) ->
            val v = ref.get()
            if (v != null) k to v else null
        }
    }
}
