package cachetype

interface GenericCache<K, V> {
    operator fun get(key: K): V?
    operator fun set(key: K, value: V)
    fun remove(key: K): V?
    fun clear()
    val size: Int

    fun entries(): List<Pair<K, V>>
}