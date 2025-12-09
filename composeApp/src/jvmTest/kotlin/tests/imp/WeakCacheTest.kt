package tests.imp

import cachetype.GenericCache
import cachetype.impl.WeakCache
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tests.BaseCacheTest

internal class WeakCacheTest : BaseCacheTest() {

    init {
        // Use WeakCache for the shared BaseCacheTest tests (Int -> Int)
        cache = WeakCache()
    }

    @Test
    fun shouldClearUnreachableItems() {
        // In this specific test, we want ByteArray values to stress memory.
        // So we use a separate local cache with V = ByteArray.
        val size = 2048
        val localCache: GenericCache<Int, ByteArray> = WeakCache()

        for (i in 0 until size) {
            localCache[i] = ByteArray(ONE_MEGABYTE)
        }

        System.gc()

        assertTrue(
            localCache.size < size,
            "Expected some entries to be cleared from WeakCache after GC"
        )
    }

    companion object {
        private const val ONE_MEGABYTE = 1024 * 1024
    }
}
