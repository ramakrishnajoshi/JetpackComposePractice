# Jetpack compose

Jetpack Compose is a modern toolkit designed to simplify UI development. It combines a reactive programming model with the conciseness and ease of use of the Kotlin programming language. It is fully declarative, meaning you describe your UI by calling a series of functions that transform data into a UI hierarchy. **When the underlying data changes, the framework automatically re-executes these functions**, updating the UI hierarchy for you.

Composable functions are Kotlin functions that are marked with the @Composable annotation.

A Compose app is made up of composable functions - just regular functions marked with @Composable, which can call other composable functions.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposePracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
```

A composable function is a regular function annotated with @Composable. This enables your function to call other @Composable functions within it. Text is a composable function provided by the library.


With Compose, an Activity remains the entry point to an Android app. You use setContent to define your layout, but instead of using an XML file as you'd do in the traditional View system, you call Composable functions within it.


### Preview
To use the Android Studio preview, you just have to mark any parameterless Composable function or functions with default parameters with the @Preview annotation and build your project. 


**Note**: When importing classes related to Jetpack Compose in this project, use those from:

* androidx.compose.* for compiler and runtime classes
* androidx.compose.ui.* for UI toolkit and libraries


### Tweaking UI

Let's start by setting a different background color for the Greeting. You can do this by wrapping the Text composable with a Surface. Surface takes a color, so use MaterialTheme.colorScheme.primary.

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }
}
```

The components nested inside Surface will be drawn on top of that background color.

You might have missed an important detail: the text is now white. When did we define this?

You didn't! The Material components, such as androidx.compose.material3.Surface, are built to make your experience better by taking care of common features that you probably want in your app, such as choosing an appropriate color for text. We say Material is opinionated because it provides good defaults and patterns that are common to most apps. The Material components in Compose are built on top of other foundational components (in androidx.compose.foundation), which are also accessible from your app components in case you need more flexibility.

In this case, Surface understands that, when the background is set to the primary color, any text on top of it should use the onPrimary color, which is also defined in the theme. You can learn more about this in the Theming your app section.


## Modifier 
`Modifier` is the primary way in Jetpack Compose to:

-   Control **size and positioning** (`width`, `height`, `size`, `fillMaxWidth`, `fillMaxSize`, `padding`, `offset`, `weight`).
-   Add **visual decorations** (`background`, `border`, `clip`, `shadow`).
-   Handle **user input** (`clickable`, `draggable`, `focusable`, `pointerInput`).
-   Add **semantic information** for accessibility and testing (`semantics`, `contentDescription`).


**Note:** Text Composable does not have onClick of its own. It has to be wrapped inside another Composable like Surface and then provide onClick callback via modifier.

Think of it like this:

-   In XML, you used attributes like `android:layout_width`, `android:padding`, `android:background`, `android:onClick`, etc., directly within the XML tag.
-   In Compose, most of these configuration aspects are handled through the `Modifier` system.

Almost every standard layout composable (like `Text`, `Image`, `Button`, `Column`, `Row`, `Box`) accepts an optional `modifier` parameter as its first optional parameter.

**Note:** `Text` Composable does not have `onClick` of its own. It has to be wrapped inside another Composable like `Surface` and then `provide onClick callback via modifier`.

**Why is `Modifier` Needed? (The Problems it Solves)**

The `Modifier` system is crucial in Compose for several key reasons:

1.  **Separation of Concerns:** It separates the _what_ (the core content or logic of a composable, like displaying text in `Text`) from the _how_ (how it looks, where it's placed, how it behaves). This makes composable functions cleaner, more focused on their primary purpose, and easier to read and maintain.
    
2.  **Reusability and Composition:**
    
    -   **Modifiers are Composable:** You can chain modifier functions together (e.g., `Modifier.padding(16.dp).background(Color.Blue)`). The order matters!
    -   **Modifiers are Reusable:** You can create common modifier combinations and reuse them across different composables, promoting consistency.
        
3.  **Extensibility without Breaking Signatures:** Imagine if every possible configuration (padding, size, background, click handling, borders, offsets, aspect ratio, weight, constraints, etc.) was a direct parameter on _every_ composable function (`Text`, `Image`, `Column`...). The function signatures would become enormous and unmanageable! The `Modifier` system provides a single, extensible parameter where new modifications can be added over time without changing the function signatures of existing composables.
    
4.  **Standardization:** It provides a consistent and standard way to apply common configurations across all types of composables. Whether you're modifying a `Text`, an `Image`, or a custom layout, the mechanism (`Modifier`) is the same.
    
5.  **Order Matters:** The order in which you apply modifiers is significant and defines how they affect the composable. For example:
    
    -   `Modifier.padding(16.dp).background(Color.Blue)`: First, padding is applied (shrinking the available space), then the background is drawn within that smaller space.
    -   `Modifier.background(Color.Blue).padding(16.dp)`: First, the background is drawn, then padding is applied _inside_ the background, pushing the content away from the edges of the background. This clear ordering makes layout predictable.
6.  **Encapsulation:** When creating your own custom composables, you should accept a `modifier` parameter and pass it along to the root composable element inside your custom component. This allows users of your custom composable to modify its external properties (like size, padding, background) without needing access to its internal implementation details.


***Note:*** Accepting a Modifier with a default value and forwarding it correctly allows your custom composables to be flexible building blocks that seamlessly integrate into any layout hierarchy, respecting modifications applied by their parent composables. Neglecting this pattern often leads to components that are rigid and difficult to reuse or position correctly in different contexts.

The Right Way of Applying Modifier:
```kotlin
@Composable
fun GoodIconContainer(
    contentDescription: String?,
    modifier: Modifier = Modifier // 1. Accept modifier
) {
    // 2. APPLY the received modifier to the Box
    Box(
        modifier = modifier // Apply the whole modifier chain from the caller!
            .background(Color.Green.copy(alpha = 0.5f)), // Apply our background *after* caller's mods
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Star, contentDescription = contentDescription)
    }
}

// --- How a Caller Might Use It ---
@Composable
fun DemonstrateGoodSizing() {
    Row(verticalAlignment = Alignment.CenterVertically) {
         // Caller gives no specific size, Box takes intrinsic Icon size + background
        GoodIconContainer("Star 1")

        // Caller ASKS for 24.dp size, GoodIconContainer RESPECTS it
        GoodIconContainer(
            "Star 2",
            modifier = Modifier.size(24.dp) // This size(24.dp) IS APPLIED!
        )

        // Caller ASKS for 60.dp size
         GoodIconContainer(
            "Star 3",
            modifier = Modifier.size(60.dp) // This size(60.dp) IS APPLIED!
        )
    }
    // Result: The three icons render in green boxes of different sizes,
    // respecting the caller's Modifier.size() instructions.
}
```

Another Example:
```kotlin
@Composable
fun GoodSquare(
    color: Color,
    modifier: Modifier = Modifier // 1. Accept modifier
) {
    // 2. APPLY the received modifier
    Box(
        modifier = modifier // Apply caller's mods (like offset)
            .size(50.dp) // Apply size *after* potential offset
            .background(color)
    )
}

// --- How a Caller Might Use It ---
@Composable
fun DemonstrateGoodOverlap() {
    Box(modifier = Modifier.size(100.dp)) { // Container
        GoodSquare(Color.Blue) // Draws at (0,0) relative to Box

        // Caller wants to OFFSET the second square, GoodSquare RESPECTS it
        GoodSquare(
            Color.Red.copy(alpha = 0.7f),
            modifier = Modifier.offset(x = 25.dp, y = 25.dp) // THIS OFFSET IS APPLIED!
        )
    }
    // Result: The Red square will draw starting at (25dp, 25dp) relative
    // to the parent Box, partially overlapping the Blue square as intended by the caller.
}
```

**Key Takeaway:**

If you don't apply the modifier parameter passed into your custom composable onto its root internal element, you break the contract with the caller. Their instructions about how your component should fit into their layout (its size, padding, offset, alignment) or how it should behave (clickable, draggable) are simply thrown away. This leads to unexpected and incorrect UI rendering, which can easily include elements being the wrong size, elements lacking spacing and thus appearing to overlap or collide, or elements literally drawing on top of each other because positioning instructions were ignored.



## Creating columns and rows
The three basic standard layout elements in Compose are Column, Row and Box.


![alt text](https://developer.android.com/static/codelabs/jetpack-compose-basics/img/518dbfad23ee1b05_1920.png)

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(modifier = modifier.padding(24.dp)) {
            Text(text = "Hello ")
            Text(text = name)
        }
    }
}
```


## State in Compose

![alt text](https://developer.android.com/static/codelabs/jetpack-compose-basics/img/6675d41779cac69.gif)

Before getting into how to make a button clickable and how to resize an item, you need to store some value somewhere that indicates whether each item is expanded or not–the **state** of the item. Since we need to have one of these values per greeting, the logical place for it is in the `Greeting` composable. Take a look at this `expanded` boolean and how it's used in the code:

```kotlin
@Composable
fun Greeting(name: String) {
    var expanded = false // Don't do this!

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Hello, ")
                Text(text = name)
            }
            ElevatedButton(
                onClick = { expanded = !expanded }
            ) {
                Text(if (expanded) "Show less" else "Show more")
            }
        }
    }
}
```

However, this **won't work** as expected. Setting a different value for the expanded variable won't make Compose detect it as a state change so nothing will happen.

**Note:** Compose apps transform data into UI by calling composable functions. If your data changes, Compose re-executes these functions with the new data, creating an updated UI—this is called **recomposition**. Compose also looks at what data is needed by an individual composable so that it only needs to recompose components whose data has changed and skip recomposing those that are not affected.


The reason why mutating this variable does not trigger recompositions is that `it's not being tracked by Compose`. Also, each time `Greeting` is called due to **recomposition**, the variable will be reset to false.

To add internal state to a composable, you can use the  `mutableStateOf`  function, which makes Compose recompose functions that read that  `State`.

**Note:** `State`  and  `MutableState`  are interfaces that hold some value and `trigger UI updates` (**recompositions**) whenever that value changes.

```kotlin
@Composable
fun Greeting() {
    val expanded = mutableStateOf(false) // Don't do this!
}
```

However  **you can't just assign** `mutableStateOf`  **to a variable inside a composable**. As explained before, `recomposition can happen at any time which would call the composable again, resetting the state to a new mutable state with a value of`  `false`.

To preserve state across recompositions, remember the mutable state using `remember`.

```kotlin
@Composable
fun Greeting(...) {
    val expanded = remember { mutableStateOf(false) }
    // ...
}
```


`remember`  is used to  **_guard_**  against recomposition, so the state is not reset.

Note that if you call the same composable from different parts of the screen you will create different UI elements, each with its own version of the state.  **You can think of internal state as a private variable in a class.**

The composable function will automatically be "subscribed" to the state. If the state changes, composables that read these fields will be recomposed to display the updates.

### Expanding the item
Now let's actually expand an item when requested. Add an additional variable that depends on our state:

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val expanded = remember { mutableStateOf(false) }

    val extraPadding = if (expanded.value) 48.dp else 0.dp
// ...
```
You don't need to remember `extraPadding` against recomposition because it's doing a simple calculation.

And now we can apply a new padding modifier to the Column:

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val expanded = remember { mutableStateOf(false) }
    val extraPadding = if (expanded.value) 48.dp else 0.dp
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding)
            ) {
                Text(text = "Hello ")
                Text(text = name)
            }
            ElevatedButton(
                onClick = { expanded.value = !expanded.value }
            ) {
                Text(if (expanded.value) "Show less" else "Show more")
            }
        }
    }
}
```

If you run on an emulator or in the interactive mode, you should see that each item can be expanded independently:

![alt text](https://developer.android.com/static/codelabs/jetpack-compose-basics/img/6675d41779cac69.gif)