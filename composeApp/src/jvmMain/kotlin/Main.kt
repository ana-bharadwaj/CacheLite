import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cachetype.GenericCache
import cachetype.impl.*
import java.util.concurrent.TimeUnit

data class CacheOperation(
    val type: String, // "PUT" or "GET"
    val key: Int,
    val value: Int? = null
)

enum class CacheType {
    PERPETUAL, FIFO, LRU, EXPIRABLE, SOFT, WEAK
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Cache Visualizer") {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                CacheVisualizerScreen()
            }
        }
    }
}

@Composable
fun CacheVisualizerScreen() {
    var selectedType by remember { mutableStateOf(CacheType.LRU) }

    var cache by remember(selectedType) {
        mutableStateOf<GenericCache<Int, Int>>(buildCache(selectedType))
    }

    var hits by remember(selectedType) { mutableStateOf(0) }
    var misses by remember(selectedType) { mutableStateOf(0) }
    var puts by remember(selectedType) { mutableStateOf(0) }
    var evictions by remember(selectedType) { mutableStateOf(0) }

    var step by remember(selectedType) { mutableStateOf(0) }
    var lastOp by remember(selectedType) { mutableStateOf("None yet") }

    // manual inputs
    var keyInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }

    // simple demo workload
    val operations = remember {
        listOf(
            CacheOperation("PUT", 1, 10),
            CacheOperation("PUT", 2, 20),
            CacheOperation("PUT", 3, 30),
            CacheOperation("GET", 1),
            CacheOperation("PUT", 4, 40),
            CacheOperation("GET", 2),
            CacheOperation("PUT", 5, 50),
            CacheOperation("GET", 3),
            CacheOperation("GET", 4),
            CacheOperation("GET", 1)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Kotlin Cache Visualizer", style = MaterialTheme.typography.h5)

        // Cache selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Cache type: ")
            Spacer(Modifier.width(8.dp))
            CacheTypeDropdown(
                selected = selectedType,
                onSelected = { newType ->
                    selectedType = newType
                    cache = buildCache(newType)
                    hits = 0; misses = 0; puts = 0; evictions = 0
                    step = 0
                    lastOp = "Switched to $newType"
                }
            )
        }

        // Demo controls
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                cache = buildCache(selectedType)
                hits = 0; misses = 0; puts = 0; evictions = 0
                step = 0
                lastOp = "Reset (new cache instance)"
            }) {
                Text("Reset")
            }

            Button(onClick = {
                if (step < operations.size) {
                    val op = operations[step]
                    when (op.type) {
                        "PUT" -> {
                            val beforeSize = cache.size
                            cache[op.key] = op.value!!
                            val afterSize = cache.size
                            if (afterSize < beforeSize) evictions++
                            puts++
                            lastOp = "DEMO PUT key=${op.key}, value=${op.value}"
                        }
                        "GET" -> {
                            val res = cache[op.key]
                            if (res == null) {
                                misses++
                                lastOp = "DEMO GET key=${op.key} → miss"
                            } else {
                                hits++
                                lastOp = "DEMO GET key=${op.key} → hit ($res)"
                            }
                        }
                    }
                    step++
                } else {
                    lastOp = "No more demo operations."
                }
            }) {
                Text("Next demo op")
            }
        }

        Text("Demo step: $step / ${operations.size}")
        Text("Last operation: $lastOp")

        Divider()

        // Manual operation panel
        Text("Manual operations", style = MaterialTheme.typography.subtitle1)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = keyInput,
                onValueChange = { keyInput = it },
                label = { Text("Key (Int)") },
                modifier = Modifier.width(120.dp)
            )
            OutlinedTextField(
                value = valueInput,
                onValueChange = { valueInput = it },
                label = { Text("Value (Int)") },
                modifier = Modifier.width(140.dp)
            )

            Button(onClick = {
                val k = keyInput.toIntOrNull()
                val v = valueInput.toIntOrNull()
                if (k != null && v != null) {
                    val beforeSize = cache.size
                    cache[k] = v
                    val afterSize = cache.size
                    if (afterSize < beforeSize) evictions++
                    puts++
                    lastOp = "MANUAL PUT key=$k, value=$v"
                } else {
                    lastOp = "Invalid key/value for PUT"
                }
            }) { Text("PUT") }

            Button(onClick = {
                val k = keyInput.toIntOrNull()
                if (k != null) {
                    val res = cache[k]
                    if (res == null) {
                        misses++
                        lastOp = "MANUAL GET key=$k → miss"
                    } else {
                        hits++
                        lastOp = "MANUAL GET key=$k → hit ($res)"
                    }
                } else {
                    lastOp = "Invalid key for GET"
                }
            }) { Text("GET") }

            Button(onClick = {
                val k = keyInput.toIntOrNull()
                if (k != null) {
                    val removed = cache.remove(k)
                    if (removed != null) {
                        lastOp = "MANUAL REMOVE key=$k (removed $removed)"
                    } else {
                        lastOp = "MANUAL REMOVE key=$k (no entry)"
                    }
                } else {
                    lastOp = "Invalid key for REMOVE"
                }
            }) { Text("REMOVE") }
        }

        Divider()

        // Metrics
        Text("Metrics", style = MaterialTheme.typography.subtitle1)
        val totalAccesses = hits + misses
        val hitRate = if (totalAccesses == 0) 0.0 else hits.toDouble() / totalAccesses * 100.0
        Text("Hits: $hits")
        Text("Misses: $misses")
        Text("Hit rate: ${"%.2f".format(hitRate)} %")
        Text("Puts: $puts")
        Text("Evictions (approx): $evictions")
        Text("Current cache size: ${cache.size}")

        // Visualization
        Text("Cache contents", style = MaterialTheme.typography.subtitle1)
        CacheBoxes(cache = cache, type = selectedType)

        // Short explanation
        CacheExplanation(selectedType)
    }
}

@Composable
fun CacheTypeDropdown(
    selected: CacheType,
    onSelected: (CacheType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected.name)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CacheType.values().forEach { type ->
                DropdownMenuItem(onClick = {
                    onSelected(type)
                    expanded = false
                }) {
                    Text(type.name)
                }
            }
        }
    }
}

@Composable
fun CacheBoxes(cache: GenericCache<Int, Int>, type: CacheType) {
    val entries = cache.entries()

    if (entries.isEmpty()) {
        Text("(empty)")
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(entries) { (k, v) ->
            val bgColor = when (type) {
                CacheType.LRU -> Color(0xFFBBDEFB)
                CacheType.FIFO -> Color(0xFFB2DFDB)
                CacheType.EXPIRABLE -> Color(0xFFFFF59D)
                CacheType.SOFT -> Color(0xFFD1C4E9)
                CacheType.WEAK -> Color(0xFFFFCCBC)
                CacheType.PERPETUAL -> Color(0xFFC8E6C9)
            }

            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 60.dp)
                    .background(bgColor)
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("K=$k")
                    Text("V=$v")
                }
            }
        }
    }
}

@Composable
fun CacheExplanation(type: CacheType) {
    Spacer(Modifier.height(8.dp))
    when (type) {
        CacheType.LRU ->
            Text("LRU: evicts least recently used entry; left → right is LRU → MRU.")
        CacheType.FIFO ->
            Text("FIFO: evicts the oldest inserted entry; left → right is oldest → newest.")
        CacheType.PERPETUAL ->
            Text("Perpetual: simple map with no eviction, only grows until cleared.")
        CacheType.EXPIRABLE ->
            Text("Expirable: clears all entries after a time interval (e.g., 5s).")
        CacheType.SOFT ->
            Text("Soft: uses SoftReferences; entries may disappear under memory pressure.")
        CacheType.WEAK ->
            Text("Weak: uses WeakHashMap; entries vanish when keys become weakly reachable.")
    }
}

fun buildCache(type: CacheType): GenericCache<Int, Int> =
    when (type) {
        CacheType.PERPETUAL ->
            PerpetualCache()
        CacheType.FIFO ->
            FifoCache(PerpetualCache(), capacity = 3)
        CacheType.LRU ->
            LruCache(capacity = 3)
        CacheType.EXPIRABLE ->
            ExpirableCache(PerpetualCache(), TimeUnit.SECONDS.toMillis(5))
        CacheType.SOFT ->
            SoftCache(PerpetualCache())
        CacheType.WEAK ->
            WeakCache()
    }
