package tests

import cachetype.GenericCache
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal abstract class BaseCacheTest {
    // NOTE: V is Any, so we can store Int, ByteArray, etc.
    protected lateinit var cache: GenericCache<Int, Any>

    @BeforeEach
    fun setup() {
        // Fill with some Int values by default
        for (i in 0..99) {
            cache[i] = i  // i is Int, which is a valid Any
        }
    }

    @AfterEach
    fun tearDown() {
        cache.clear()
    }

    @Test
    fun shouldClearAllEntries() {
        Assertions.assertTrue(cache.size > 0)

        cache.clear()

        Assertions.assertEquals(0, cache.size)
    }

    @Test
    open fun shouldRemoveEntry() {
        Assertions.assertNotNull(cache[1])

        cache.remove(1)

        Assertions.assertNull(cache[1])
    }
}