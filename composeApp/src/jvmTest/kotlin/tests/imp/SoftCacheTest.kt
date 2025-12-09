package tests.imp
import tests.BaseCacheTest

import cachetype.impl.PerpetualCache
import cachetype.impl.SoftCache
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SoftCacheTest : BaseCacheTest() {

    init {
        // SoftCache needs a delegate → give it a PerpetualCache<Int, Any>
        cache = SoftCache(PerpetualCache())
    }

    @Test
    fun shouldClearUnreachableItems() {
        // We don’t care about BaseCacheTest’s setup values for this test
        cache.clear()

        val size = 2048 * 2
        for (i in 0 until size) {
            // Now V is Any, so ByteArray is perfectly allowed
            cache[i] = ByteArray(ONE_MEGABYTE)
        }

        // Hint GC to run; not guaranteed, but typically some SoftReferences are cleared
        System.gc()

        assertTrue(
            cache.size < size,
            "Expected some SoftReference-backed entries to be cleared by GC"
        )
    }

    companion object {
        private const val ONE_MEGABYTE = 1024 * 1024
    }
}
