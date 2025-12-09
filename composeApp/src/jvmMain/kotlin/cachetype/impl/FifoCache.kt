package cachetype.impl

import cachetype.GenericCache

class FifoCache<K, V>(
    private val delegate: GenericCache<K, V>,
    private val capacity: Int
) : GenericCache<K, V> by delegate {

    private val order = ArrayDeque<K>()  // keeps insertion order

    override fun set(key: K, value: V) {
        // If it's a new key, record its insertion order
        if (delegate.get(key) == null) {
            order.addLast(key)
        }
        delegate.set(key, value)
        evictIfNeeded()
    }

    override fun remove(key: K): V? {
        order.remove(key)
        return delegate.remove(key)
    }

    private fun evictIfNeeded() {
        while (delegate.size > capacity && order.isNotEmpty()) {
            val oldestKey = order.removeFirst()
            delegate.remove(oldestKey)
        }
    }

    override fun entries(): List<Pair<K, V>> =
        // Use FIFO order: oldest → newest
        order.mapNotNull { k ->
            delegate[k]?.let { v -> k to v }
        }

    override fun toString(): String {
        // Show order visually: oldest → newest
        val entries = order.map { k -> "$k:${delegate[k]}" }
        return entries.joinToString(prefix = "[", postfix = "]", separator = " | ")
    }
}
