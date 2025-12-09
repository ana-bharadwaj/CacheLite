package tests.imp
import tests.BaseCacheTest
import cachetype.*
import cachetype.impl.PerpetualCache
internal class PerpetualCacheTest : BaseCacheTest() {
    init {
        cache = PerpetualCache()
    }
    // Inherits generic tests from BaseCacheTest
}
