Got it, let’s make a clean, short `README.md` version 👇
You can copy-paste this as is.

---

# Kotlin Cache Visualizer

Interactive **Kotlin + Compose Desktop** app to visualize how different cache strategies work.

---

## Overview

This app lets you:

* Switch between cache types:

  * **Perpetual** – simple map, no eviction
  * **FIFO** – evicts the *oldest inserted* item
  * **LRU** – evicts the *least recently used* item
  * **Expirable** – clears entries after a time interval
  * **Soft** – uses `SoftReference`, entries can be dropped by GC
  * **Weak** – uses `WeakHashMap`, entries vanish when keys are weakly reachable

* See the **cache contents as colored boxes**:

  * Keys and values
  * Order reflects the policy (e.g., LRU: least → most recently used)

* Track basic **metrics**:

  * Hits / Misses
  * Hit rate
  * Number of PUTs
  * Approximate evictions
  * Current cache size

* Run:

  * A built-in **demo sequence** of PUT/GET operations
  * **Manual operations** by entering a key and value (PUT/GET/REMOVE)

---

## How it’s built

* Common `GenericCache<K, V>` interface with:

  * `get`, `set`, `remove`, `clear`, `size`, and `entries()` for visualization
* Separate implementations for each policy:

  * `PerpetualCache`, `FifoCache`, `LruCache`, `ExpirableCache`, `SoftCache`, `WeakCache`
* UI is built with **Compose Desktop**:

  * Cache type dropdown
  * Controls for demo + manual operations
  * Metrics panel
  * Visual row of cache entries (boxes) and a short text explanation per policy
 
 ## How to run

### Prerequisites

- JDK 17 (or compatible)
- Gradle (or use the Gradle wrapper)
- IntelliJ IDEA (recommended) with Kotlin support

### Option 1: Run from IntelliJ

1. Open the project folder in **IntelliJ IDEA**.
2. Let Gradle sync finish.
3. Open `Main.kt` (the file containing `fun main()`).
4. Click the **Run**  button next to `fun main()` (or use `Run → Run 'MainKt'`).
5. A desktop window titled **“Cache Visualizer”** should appear.

