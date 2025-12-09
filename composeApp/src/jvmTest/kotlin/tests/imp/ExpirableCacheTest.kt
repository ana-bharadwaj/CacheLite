package tests.imp
import tests.BaseCacheTest
import cachetype.impl.*
import cachetype.*

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class ExpirableCacheTest : BaseCacheTest() {
    init {
        cache = ExpirableCache(PerpetualCache(), TimeUnit.SECONDS.toMillis(1))
    }

    @Test
    fun shouldExpire() {
        // Wait slightly more than 1 second so recycle() fires
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 100)

        assertEquals(0, cache.size, "Cache should be empty after expiration interval")
    }

    @Test
    fun shouldExpireMultipleTimes() {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 100)
        assertEquals(0, cache.size)

        cache.set(1, 1)
        assertEquals(1, cache.size)

        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 100)
        assertEquals(0, cache.size)
    }
}
