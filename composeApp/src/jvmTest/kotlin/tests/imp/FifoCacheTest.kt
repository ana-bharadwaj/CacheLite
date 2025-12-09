package tests.imp
import tests.*
import cachetype.impl.*

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FifoCacheTest : BaseCacheTest() {
    init {
        cache = FifoCache(PerpetualCache(), capacity = 3)
    }

    @Test
    fun shouldEvictOldestEntryWhenCapacityExceeded() {
        // Ignore BaseCacheTest’s setup state; start clean for this scenario
        cache.clear()

        cache.set(1, 1)
        cache.set(2, 2)
        cache.set(3, 3)
        assertEquals(3, cache.size)

        // Adding one more should evict key=1 (FIFO)
        cache.set(4, 4)

        assertEquals(3, cache.size)
        assertNull(cache[1], "Oldest key (1) should have been evicted")
        assertNotNull(cache[2])
        assertNotNull(cache[3])
        assertNotNull(cache[4])
    }
}
