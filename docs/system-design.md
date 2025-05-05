### Performance Considerations

-   UI responsiveness and rendering optimization
-   Memory management (avoiding leaks)
-   Battery efficiency
-   Network efficiency (caching, prefetching)
-   Storage strategies


### Search AutoComplete

```kotlin
private fun setupSearchDebounce() {
        compositeDisposable.add(
            querySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    query -> performSearch(query)
                }
        )
    }
```

## Debounce Purpose:
Prevents excessive API calls when user is typing quickly
Waits for 300ms of inactivity before processing the search

```
  User types: "sta" → "stac" → "stack"
  Without debounce: 3 API calls (for "sta", "stac", "stack")
  With debounce: 1 API call (only for "stack" after 300ms pause)
```

## Distinct Until Changed:
Prevents duplicate API calls for the same search term
```
  User types: "stack" → backspace → "stack"
  Without distinct: 2 API calls
  With distinct: 1 API call (ignores duplicate "stack")
```


```
User typing: "stack overflow"
│
├─ s → Debounce waiting...
├─ t → Debounce waiting...
├─ a → Debounce waiting...
├─ c → Debounce waiting...
├─ k → Debounce waiting...
│   [300ms pause]
├─ First API call for "stack"
│
├─ o → Debounce waiting...
├─ v → Debounce waiting...
├─ e → Debounce waiting...
├─ r → Debounce waiting...
│   [300ms pause]
└─ Second API call for "stack over"
```


Why is it needed?

1.  Performance:

-   Reduces unnecessary API calls

-   Saves server resources

-   Reduces mobile data usage

2.  User Experience:

-   Smoother search experience

-   No UI lag during typing

-   More responsive interface

3.  Resource Management:

-  Prevents overwhelming the server

-  Manages thread usage efficiently

-  Proper cleanup with compositeDisposable

```kotlin
// Without debounce (problematic)
searchField.addTextChangedListener { text ->
    // Makes API call for EVERY keystroke
    performSearch(text)  // Could make 10+ API calls while typing "stackoverflow"
}

// With debounce (optimized)
searchField.textChanges()
    .debounce(300, TimeUnit.MILLISECONDS)
    .distinctUntilChanged()
    .subscribe { text ->
        // Makes API call only after user stops typing
        performSearch(text)  // Makes 1-2 API calls for typing "stackoverflow"
    }
```


```kotlin
   // Type-ahead specific optimizations
   .debounce(300, TimeUnit.MILLISECONDS)  // Manage API calls
   .filter { it.length >= 2 }            // Reduce unnecessary searches
   .distinctUntilChanged()               // Avoid duplicates
```

## Local storage to provide first level search functionality,

A Trie (pronounced "try") is a tree-like data structure used to store a dynamic set of strings, typically words from a dictionary. It's highly efficient for prefix-based searching.

### How Data Is Stored
Each character of a word is stored as a level in the Trie.

A path from the root to a node represents a prefix or a full word.

For example, the word "cat" is stored as: root -> c -> a -> t with t node marked as the end of a word.

```
         root
        /    \
      c        b
     /          \
    a            a
   / \            \
  t   n            t
(end)(end)        (end)
```

| Use                           | Approach                 |
| ----------------------------- | ------------------------ |
| Text-heavy, persistent search | `Room + FTS`             |
| Fast, in-memory prefix search | `Trie`                   |
| Simple LIKE search            | `Room with LIKE queries` |



## **Improvements / Scalability Options**

-   Add a **limit `k`** to restrict number of results
    
-   Track **frequency** of word usage in Trie for better ranking
    
-   Use a **min-heap** or **priority queue** to return top `k` popular terms
    
-   For large-scale apps, move to **inverted index** or **Lucene-based search backend**



> 💡 **Trie is naturally built for prefix-based lookup** — the core requirement of autocomplete.


## ❓ Why Use Trie over HashMap or List for Autocomplete?

✅ 1. Search Performance

| Structure        | Prefix Search Time | Why                                           |
| ---------------- | ------------------ | --------------------------------------------- |
| **Trie**         | **O(P)**           | Walk the prefix character by character        |
| **HashMap/List** | **O(N·P)**         | Must check all `N` words and compare prefixes |

✅ 3. Space Efficiency
Trie: Can reuse shared prefixes, so "app", "apple", "application" share "app" path.

HashMap/List: Each word is stored independently, which is redundant.

## ⚔️ Trie vs Room with FTS for Autocomplete

| Criteria                         | **Trie**                                        | **Room with FTS**                           |
| -------------------------------- | ----------------------------------------------- | ------------------------------------------- |
| **Latency**                      | ⚡ Fastest (in-memory, O(P + K))                 | ⚠️ Slower (SQL query, disk access)          |
| **Prefix search**                | ✅ Built-in by design                            | ✅ Supported via `MATCH 'prefix*'`           |
| **Offline Support**              | ✅ Fully offline                                 | ✅ Fully offline                             |
| **Persistence**                  | ❌ Not persistent by default                     | ✅ Data stored on disk                       |
| **Scalability (data size)**      | ⚠️ Limited by RAM                               | ✅ Handles large datasets better             |
| **Ranking by frequency/recency** | ❌ Manual                                        | ✅ Built-in scoring (`bm25()`)               |
| **Autocomplete on typing**       | ✅ Fast enough for every keystroke               | ⚠️ Might need debounce or async             |
| **Code complexity**              | ✅ Easy logic, no DB schema                      | ⚠️ Requires Room setup & syncing            |
| **Data update sync**             | ❌ Manual update to Trie                         | ✅ Automatic with `contentEntity`/trigger    |
| **Use Case Fit**                 | 🔥 Great for UI autocomplete (e.g., search bar) | 🔥 Great for text search in large documents |


## 🔍 When to Use **Trie**

✅ Best for:

-   Fast, real-time suggestions on each keystroke
    
-   Limited dataset (e.g., <10k entries)
    
-   Use in memory (e.g., recent search history, cached terms)
    
-   Autocomplete before any DB sync
    

🚫 Avoid if:

-   You have millions of entries
    
-   Need persistence or scoring by relevance
    

----------

## 🧠 When to Use **Room + FTS**

✅ Best for:

-   Searching large, persistent datasets (notes, messages)
    
-   Need relevance scoring (`bm25` ranking)
    
-   Want to store/search long-form text fields
    
-   Need to sync with backend
    

🚫 Avoid if:

-   You need sub-100ms latency per keystroke for live suggestions
    
-   You’re only filtering small known lists

### 🌀 Hybrid Pattern (Best of Both)
Use Trie for real-time prefix autocomplete (e.g., showing cached suggestions as user types)

Use Room FTS to fetch relevant, ranked, full-text search results when user submits



Questions on Multi module architecture:

Use exclude to remove unwanted transitive deps:

```kotlin
implementation("libA") {
    exclude(group = "com.google.code.gson", module = "gson")
}
```


### **How does Gradle resolve version conflicts?**

> ✅ **Answer:**  
> Gradle uses **conflict resolution strategy**:

-   Picks the **highest version** across all modules unless a strict rule is defined.
    
-   You can override this using:
    

    
```kotlin
configurations.all {
    resolutionStrategy {
       force("com.google.code.gson:gson:2.10.1")
    }
} 
```    

> 🧠 Interview Tip: Mention `resolutionStrategy.force()` and `dependency constraints` as ways to control resolution.



### 📦 What are Transitive Dependencies?
Definition: A transitive dependency is a dependency that your dependency depends on.

🔁 In simple terms:
If Module A depends on Library B, and Library B depends on Library C,
then Module A gets Library C as a transitive dependency.

```
// In your module's build.gradle:
implementation("com.squareup.retrofit2:retrofit:2.9.0")
```

Retrofit internally depends on:

-   `okhttp`
    
-   `gson` (optionally via converter)
    

So even if **you didn’t declare Gson**, it might still end up in your build — **transitively**.



### ❗ Why do transitive dependencies matter?

-   **Version conflicts**: two libraries pulling in different versions of the same dependency
    
-   **Increased APK size**: unwanted or unused libraries included
    
-   **Hidden dependencies**: hard to trace where something came from

## Structure of Login Feature in Multi Module Architecture


## 🧱 Why Separate `data-login` from `feature-login`?

> **Separation of concerns + independence of layers**  
> `feature-login` handles **UI and interaction**,  
> `data-login` handles **network/database implementation** for login.

This allows:

-   Independent testing
    
-   Replaceability (e.g., mock `AuthRepository` in tests)
    
-   Swappable data sources (local, remote)
    
-   Separation of UI vs data logic



## 🧩 Module Breakdown: Login Feature Example

Here’s a Clean Modular Architecture with 4 logical module types:

----------

### 🔹 `:feature-login` (UI Layer)

> **What it is**: UI and ViewModel for login screen

**Contents:**

-   `LoginFragment`, `LoginViewModel`
    
-   UI state management (e.g., `LoginUiState`)
    
-   Depends on: `domain-login` for `AuthRepository`
    
-   No Retrofit, no Room
    

----------

### 🔹 `:domain-login` (Use Case / Business Logic Layer)

> **What it is**: Business logic, interfaces

**Contents:**

-   `AuthRepository` interface
    
-   Use case: `LoginWithGoogleUseCase`
    
-   Data models (e.g., `User`, `Token`)
    
-   Pure Kotlin (no Android/Retrofit/Room dependencies)
    
-   Used by both `feature-login` and `data-login`
    

----------

### 🔹 `:data-login` (Data Layer)

> **What it is**: Implements repositories using Retrofit/Room/etc.

**Contents:**

-   `AuthRepositoryImpl` (implements interface from domain)
    
-   Uses Retrofit/Room to talk to API/local storage
    
-   Depends on `core-network`, `core-database`
    
-   No UI logic
    

----------

### 🔹 `:core-network` / `:core-database` (Infra Layer)

> Shared infrastructure for all features

**Contents:**

-   Retrofit builder, interceptors, API interfaces
    
-   Room DB, DAOs
    
-   Gson converters
    
-   Dependency Injection bindings (Hilt modules)



## Dependency Graph
```
           [ :app ]
              ↓
      ┌──────────────┐
      ↓              ↓
[ :feature-login ]  [ :data-login ]
        ↓                ↓
 [ :domain-login ] ←─────┘
        ↓
[ :core-network / :core-database ]
```


> 🔁 Data flows from `core → data → domain → feature → app`

## Module Summary Table
| Module           | Contains                                            | Depends On                       |
| ---------------- | --------------------------------------------------- | -------------------------------- |
| `:app`           | DI setup, navigation, entry point                   | All feature modules              |
| `:feature-login` | UI, ViewModel, screen state                         | `:domain-login`                  |
| `:domain-login`  | Interfaces, business rules (e.g., `AuthRepository`) | None                             |
| `:data-login`    | Repository implementation using Retrofit            | `:domain-login`, `:core-network` |
| `:core-network`  | Retrofit, interceptors, Gson                        | Retrofit, OkHttp, Gson           |
| `:core-database` | Room setup, DB config                               | Room                             |


### ✅ Notes:

-   `:core-models` is not strictly required if data models are tightly scoped and reused only within login.
    
-   If models are shared across features, reintroducing `:core-models` may be better.



### **Can you explain the **Cache Eviction Policies** and how they affect cache invalidation?**

-   **LRU (Least Recently Used)**: Evicts the least recently used items when the cache reaches its limit.
    
-   **LFU (Least Frequently Used)**: Evicts the least frequently used items.
    
-   **FIFO (First In First Out)**: Evicts the oldest cache entries.



## Common Cache Invalidation Strategies to Discuss

### **1. Time-Based Expiration (TTL)**

-   **TTL** ensures that cached data is only valid for a set period.
    
    -   Example: **24 hours** for a product list, or **1 hour** for inventory data.
        
    -   **Pros**: Simple to implement.
        
    -   **Cons**: May serve stale data if the TTL is too long.
        

### **2. Event-Based Invalidations**

-   Cache is invalidated or refreshed based on specific events (e.g., new data comes in, user performs an action, etc.).
    
    -   Example: Cache for banners could be invalidated when the admin updates a campaign.
        
    -   **Pros**: Accurate, up-to-date data.
        
    -   **Cons**: More complex to implement with multiple dependencies.
        

### **3. Manual Invalidations**

-   Programmatically trigger cache invalidation based on business rules or conditions.
    
    -   Example: Explicitly clearing cache when a user logs out or a session expires.
        
    -   **Pros**: Full control over cache.
        
    -   **Cons**: Potentially forgetting to invalidate at the right moments.
        

### **4. Cache Versioning**

-   Cache data with a version identifier to manage invalidation when the schema changes or the data source changes.
    
    -   Example: Every time the API schema changes, increment the version.
        
    -   **Pros**: Helps handle schema changes in the cache.
        
    -   **Cons**: Adds complexity.