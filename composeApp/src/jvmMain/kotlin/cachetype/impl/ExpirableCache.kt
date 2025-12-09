package cachetype.impl

import cachetype.GenericCache
import java.util.concurrent.TimeUnit

class ExpirableCache<K, V>(
    private val delegate: GenericCache<K, V>,
    private val flushIntervalMillis: Long = TimeUnit.MINUTES.toMillis(1)
) : GenericCache<K, V> by delegate {

    private var lastFlushTime = System.nanoTime()

    override fun get(key: K): V? {
        recycle()
        return delegate[key]
    }

    override fun set(key: K, value: V) {
        recycle()
        delegate[key] = value
    }

    override fun remove(key: K): V? {
        recycle()
        return delegate.remove(key)
    }

    override val size: Int
        get() {
            recycle()
            return delegate.size
        }

    override fun clear() {
        delegate.clear()
        lastFlushTime = System.nanoTime()
    }

    override fun entries(): List<Pair<K, V>> {
        recycle()
        return delegate.entries()
    }

    private fun recycle() {
        val now = System.nanoTime()
        val intervalNanos = TimeUnit.MILLISECONDS.toNanos(flushIntervalMillis)
        if (now - lastFlushTime >= intervalNanos) {
            delegate.clear()
            lastFlushTime = now
        }
    }

    override fun toString(): String {
        return "Expirable(${delegate.toString()})"
    }
}
