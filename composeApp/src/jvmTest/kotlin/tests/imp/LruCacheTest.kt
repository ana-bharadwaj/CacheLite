package tests.imp

import tests.BaseCacheTest
import cachetype.impl.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LruCacheTest : BaseCacheTest() {
    init {
        cache = LruCache(capacity = 3)
    }

    @Test
    fun shouldEvictLeastRecentlyUsedEntry() {
        cache.clear()

        // Insert 3 entries
        cache.set(1, 1)
        cache.set(2, 2)
        cache.set(3, 3)

        // Access 1 and 2, so 3 becomes LRU
        cache[1]
        cache[2]

        // Adding 4 should evict key=3
        cache.set(4, 4)

        assertNull(cache[3], "Key 3 should be evicted as the least recently used")
        assertNotNull(cache[1])
        assertNotNull(cache[2])
        assertNotNull(cache[4])
    }
}
