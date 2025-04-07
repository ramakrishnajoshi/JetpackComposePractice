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

In Jetpack Compose, the Text composable itself doesn't have a direct onClick parameter like a Button does. To make a Text clickable, you use the clickable Modifier.
```kotlin
@Composable
fun ClickableTextExample() {
    Text(
        text = "Click Me!",
        modifier = Modifier.clickable {
            println("Text was clicked!")
            // Add your click handling logic
        }
    )
}
```

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

Note that if you call the same composable from different parts of the screen you will create **different UI elements**, each with its own version of the state.  **You can think of internal state as a private variable in a class.**

The composable function will automatically be "subscribed" to the state. **If the state changes, composables that read these fields will be recomposed to display the updates.**

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




# State hoisting
In Composable functions, state that is read or modified by multiple functions should live in a common ancestor—this process is called **state hoisting**. To hoist means to lift or elevate.

Making state hoistable 
* avoids duplicating state and introducing bugs, 
* helps reuse composables, and makes composables substantially easier to test. 

Contrarily, state that **doesn't need to be controlled by a composable's parent** should not be **hoisted**. The **source of truth** belongs to whoever creates and controls that state.

For example, let's create an onboarding screen for our app.
![alt text](https://developer.android.com/static/codelabs/jetpack-compose-basics/img/5d5f44508fcfa779_1920.png)

```kotlin
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    // TODO: This state should be hoisted
    var shouldShowOnboarding by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = { shouldShowOnboarding = false } 
        ) {
            Text("Continue")
        }
    }
}
```
This code contains a bunch of new features:

* `shouldShowOnboarding` is using a `by` keyword instead of the `=`. This is a property delegate that saves you from typing `.value` every time.
* When the button is clicked, shouldShowOnboarding is set to false, however you are not reading the state from anywhere yet.

Now we can add this new onboarding screen to our app. We want to show it on launch and then hide it when the user presses "Continue".

For example to show the onboarding screen or the list of greetings you would do something like:
```kotlin
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    Surface(modifier) {
        if (shouldShowOnboarding) { // We don't have access of shouldShowOnboarding though
            OnboardingScreen()
        } else {
            Greetings()
        }
    }
}
```
However we don't have access to  `shouldShowOnboarding`  . It's clear that we need to share the state that we created in  `OnboardingScreen`  with the  `MyApp`  composable.

Instead of somehow sharing the value of the state with its parent, we  **_hoist_**  the state–we simply move it to the common ancestor that needs to access it.

Now add the logic to show the different screens in MyApp, and hoist the state.

```kotlin
@Composable
fun MyApp(modifier: Modifier = Modifier) {

    var shouldShowOnboarding by remember { mutableStateOf(true) }

    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(/* TODO */)
        } else {
            Greetings()
        }
    }
}
```

We also need to share  `shouldShowOnboarding`  with the onboarding screen but we are not going to pass it directly. Instead of letting  `OnboardingScreen`  mutate our state, **it would be better to let it notify us when the user clicked on the  _Continue_  button.**

How do we pass events up? By  **passing callbacks down**. Callbacks are functions that are passed as arguments to other functions and get executed when the event occurs.

Try to add a function parameter to the onboarding screen defined as  `onContinueClicked: () -> Unit`  so you can mutate the state from  `MyApp`.

Solution:

```kotlin
@Composable
fun MyApp(modifier: Modifier = Modifier) {

    var shouldShowOnboarding by remember { mutableStateOf(true) }

    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(
                onContinueClicked = { // passing callback down
                    shouldShowOnboarding = false 
                }
            )
        } else {
            Greetings()
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier
                .padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }

}
```

By passing a function and not a state to  `OnboardingScreen`  we are making this composable more reusable and protecting the state from being mutated by other composables. In general, it keeps things simple. A good example is how the onboarding preview needs to be modified to call the  `OnboardingScreen`  now:

```kotlin
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    BasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = {}) // Do nothing on click.
    }
}
```

Assigning  `onContinueClicked`  to an empty lambda expression means "do nothing", which is perfect for a preview.





# Creating a performant lazy list

To display a scrollable column we use a LazyColumn. LazyColumn renders only the visible items on screen, allowing performance gains when rendering a big list.

**Note**:  `LazyColumn`  and  `LazyRow`  are equivalent to  `RecyclerView`  in Android Views.




## Persisting state
Data needs to be persisted in below scenarios:
* configuration changes (such as rotations),
* process death
* when LazyColumn item goes out of screen height


### remember vs rememberSaveable

If you run the app on a device, click on the buttons and then you rotate, the onboarding screen is shown again. The  `remember`  function works  **only as long as the composable is kept in the Composition**. **When you rotate, the whole activity is restarted so all state is lost**. This also happens with **any configuration change and on process death.**

Instead of using  `remember`  you can use  `rememberSaveable`. **This will save each state surviving configuration changes (such as rotations) and process death.**

```kotlin
import androidx.compose.runtime.saveable.rememberSaveable
// ...

var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
```
Run, rotate, change to dark mode or kill the process. The onboarding screen is not shown unless you have previously exited the app.

### Persisting the expanded state of the list items

If you expand a list item and then either scroll the list until the item is out of view, or rotate the device and then go back to the expanded item, you'll see that the item is now back to its initial state.

The solution for this is to use rememberSaveable for the expanded state as well:
```
var expanded by rememberSaveable { mutableStateOf(false) }
```


## Styling and theming your app
If you open the ui/theme/Theme.kt file, you see that BasicsCodelabTheme uses MaterialTheme in its implementation:

```kotlin
@Composable
fun BasicsCodelabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // ...

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

Because  `BasicsCodelabTheme`  wraps  `MaterialTheme`  internally,  `MyApp`  is styled with the properties defined in the theme. From any descendant composable you can retrieve three properties of  `MaterialTheme`:  
* `colorScheme`,  
* `typography`  and  
* `shapes`. 

Use them to set a header style for one of your  `Text`s:

```kotlin
    Column(modifier = Modifier
            .weight(1f)
            .padding(bottom = extraPadding.coerceAtLeast(0.dp))
    ) {
            Text(text = "Hello, ")
            Text(text = name, style = MaterialTheme.typography.headlineMedium)
        }
```

Sometimes you need to deviate slightly from the selection of colors and font styles. In those situations it's better to base your color or style on an existing one.

For this, you can modify a predefined style by using the `copy` function. Make the number extra bold:
```kotlin
import androidx.compose.ui.text.font.FontWeight
// ...
Text(
    text = name,
    style = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.ExtraBold
    )
)
```



# Design

![main screen](https://developer.android.com/static/codelabs/jetpack-compose-layouts/img/9825de962ae22604_1920.png)


First let's design below ui:
![alt text](https://developer.android.com/static/codelabs/jetpack-compose-layouts/img/ea3d96db9dd6c062_1920.png)

The image also needs to be scaled correctly. To do so, we can use the Image's contentScale parameter. There are several options, most notably:

![alt text](https://developer.android.com/static/codelabs/jetpack-compose-layouts/img/5f17f07fcd0f1dc_1920.png)

In general, to align composables inside a parent container, you set the  **alignment**  of that parent container. So instead of telling the child to position itself in its parent, you tell the parent how to align its children.

For a  `Column`, you decide how its children should be aligned horizontally. The options are:

-   Start
-   CenterHorizontally
-   End

For a  `Row`, you set the vertical alignment. The options are similar to those of the  `Column`:

-   Top
-   CenterVertically
-   Bottom

For a  `Box`, you combine both horizontal and vertical alignment. The options are:

-   TopStart
-   TopCenter
-   TopEnd
-   CenterStart
-   Center
-   CenterEnd
-   BottomStart
-   BottomCenter
-   BottomEnd


All of the container's children will follow this same alignment pattern. You can override the behavior of a single child by adding an `align` modifier to it.

```kotlin
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

@Composable
fun AlignYourBodyElement(
   @DrawableRes drawable: Int,
   @StringRes text: Int,
   modifier: Modifier = Modifier
) {
   Column(
       modifier = modifier,
       horizontalAlignment = Alignment.CenterHorizontally
   ) {
       Image(
           painter = painterResource(drawable),
           contentDescription = null,
           contentScale = ContentScale.Crop,
           modifier = Modifier
               .size(88.dp)
               .clip(CircleShape)
       )
       Text(
           text = stringResource(text),
           modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
           style = MaterialTheme.typography.bodyMedium
       )
   }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun AlignYourBodyElementPreview() {
   MySootheTheme {
       AlignYourBodyElement(
           text = R.string.ab1_inversions,
           drawable = R.drawable.ab1_inversions,
           modifier = Modifier.padding(8.dp)
       )
   }
}
```

Let's design below UI now:
![alt text](https://developer.android.com/static/codelabs/jetpack-compose-layouts/img/52e72a19e67f646d_1920.png)

![alt text](https://developer.android.com/static/codelabs/jetpack-compose-layouts/img/b5a11ff3afd99c09_1920.png)

This container uses surfaceVariant as its background color which is different from the background of the whole screen. It also has rounded corners. We specify these for the favorite collection card using Material's Surface composable.

You can adapt the Surface to your needs by setting its parameters and modifier. In this case, the surface should have rounded corners. You can use the shape parameter for this. Instead of setting the shape to a Shape as for the Image in the previous step, you'll use a value coming from our Material theme.

```kotlin
@Composable
fun FavoriteCollectionCard(
   @DrawableRes drawable: Int,
   @StringRes text: Int,
   modifier: Modifier = Modifier
) {
   Surface(
       shape = MaterialTheme.shapes.medium,
       color = MaterialTheme.colorScheme.surfaceVariant,
       modifier = modifier
   ) {
       Row(
           verticalAlignment = Alignment.CenterVertically,
           modifier = Modifier.width(255.dp)
       ) {
           Image(
               painter = painterResource(drawable),
               contentDescription = null,
               contentScale = ContentScale.Crop,
               modifier = Modifier.size(80.dp)
           )
           Text(
               text = stringResource(text),
               style = MaterialTheme.typography.titleMedium,
               modifier = Modifier.padding(horizontal = 16.dp)
           )
       }
   }
}


//..


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun FavoriteCollectionCardPreview() {
   MySootheTheme {
       FavoriteCollectionCard(
           text = R.string.fc2_nature_meditations,
           drawable = R.drawable.fc2_nature_meditations,
           modifier = Modifier.padding(8.dp)
       )
   }
}
```




## Approaches to Hide Views in Jetpack Compose

### 1. Conditional Rendering
**Best for:** Complete removal of components
```kotlin
if (shouldShow) {
    Text("Only visible when shouldShow is true")
}
```
- Component isn't included in the composition when hidden
- No layout space taken when hidden
- Most efficient approach for toggling visibility

### 2. Alpha Modification
**Best for:** Animations or temporarily hiding while preserving layout
```kotlin
Text(
    text = "Invisible but maintains layout space",
    modifier = Modifier.alpha(if (isVisible) 1f else 0f)
)
```
- Component maintains its position and size
- Still participates in layout measurement
- Can be animated smoothly

### 3. Size Modification
**Best for:** Collapsing elements without removing them
```kotlin
Box(
    modifier = Modifier.size(if (isVisible) 100.dp else 0.dp)
)
```
- Component shrinks to zero size when hidden
- Still exists in composition
- Can be used with animated size changes

### 4. Custom Layout Modifier
**Best for:** Complex visibility behaviors
```kotlin
Modifier.layout { measurable, constraints ->
    if (isVisible) {
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    } else {
        layout(0, 0) {}
    }
}
```
- Most flexible approach
- Allows custom layout logic based on visibility
- Higher implementation complexity


## Row
* **What it is**: Row is a layout composable that arranges its children horizontally, one after the other, in a single row. Think of it like a `LinearLayout` with `orientation="horizontal"` in the traditional Android View system.
* **Key Parameters:**
* `modifier`: Used to apply size constraints, padding, background, click listeners, etc., to the Row itself.
* `horizontalArrangement`: Controls how the children are spaced out along the horizontal axis within the Row. Common options include `Arrangement.Start, Arrangement.End, Arrangement.Center, Arrangement.SpaceBetween, Arrangement.SpaceAround, Arrangement.SpaceEvenly`.
* `verticalAlignment`: Controls how children are aligned along the vertical axis within the Row. Common options include `Alignment.Top, Alignment.CenterVertically, Alignment.Bottom`.

## Column Composable

* **What it is:** `Column` is a layout composable that arranges its children `vertically`, one below the other, in a single column. Think of it like a `LinearLayout` with `orientation="vertical"` in the traditional Android View system.
* **Key Parameters:**
* `modifier`: Standard modifier for size, padding, background, etc.
* `verticalArrangement`: Controls how the children are spaced out along the vertical axis within the Column. Common options include `Arrangement.Top, Arrangement.Bottom, Arrangement.Center, Arrangement.SpaceBetween, Arrangement.SpaceAround, Arrangement.SpaceEvenly.`
`horizontalAlignment`: Controls how children are aligned along the horizontal axis within the Column. Common options include `Alignment.Start, Alignment.CenterHorizontally, Alignment.End.`

* **When to Use It:**
* Use Col`umn whenever you need to display a list of items vertically.
* It's the go-to for simple top-to-bottom layouts like forms, lists of settings, or sections stacked vertically on a screen.
* It's efficient and easy to understand for basic vertical sequences.

```kotlin
Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp), // Add space between items
    horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
) {
    Text("Item 1")
    Text("Item 2")
    Button(onClick = { /* ... */ }) {
        Text("A Button")
    }
}
```

Understanding why below `Column` in below code takes entire screen size.

```kotlin
@Preview(showBackground = true, widthDp = 350, heightDp = 700)
@Composable
fun ColumnALignmentPreview() {
    JetpackComposePracticeTheme {
        Column(
            modifier = Modifier.background(color = Color.Yellow),

            ) {
            Text(
                text = "Hello Text 1",
                modifier = Modifier.background(color = Color.Gray)
            )
            Text(
                text = "Hello Text 2",
                modifier = Modifier.background(color = Color.Cyan)
            )
        }
    }
}
```

![alt text](https://file%2B.vscode-resource.vscode-cdn.net/Users/ramakrishnajoshi/Documents/FlutterProjects/JetpackComposePractice/docs/images-for-doc/Screenshot%202025-04-06%20at%206.41.02%20PM.png?version%3D1743945139772)

* **Column Measurement:** The `Column` measures its children ( like `Text` elements). Its width becomes the width of the wider Text, and its height becomes the sum of the Text heights. Let's say this measured size is `W x H`.
* **Modifier.background:** This modifier is applied to the Column. It draws the primary color only within the measured bounds (`W x H`) of the Column.
* **Parent Container (JetpackComposePracticeTheme):** The `Column` sits inside `JetpackComposePracticeTheme`. Themes often wrap their content in a `Surface`. **A Surface placed directly inside the setContent block (or the preview's root) often implicitly tries to fill the maximum available size given by its parent** (the preview area in this case).



## ConstraintLayout in Jetpack Compose
**What it is:** `ConstraintLayout` is a powerful and flexible layout composable that allows you to position children relative to each other or to the parent container using constraints. It helps create complex UIs with a **flatter view hierarchy** compared to deeply nested Rows and Columns. It's analogous to `ConstraintLayout` in the `XML` view system.

It's available as a separate artifact that you need to add to your dependencies:
```groovy
// build.gradle.kts (app level)
dependencies {
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
}
```


**Why is it needed?** While `Row`, `Column`, and `Box` are powerful for many layouts, ConstraintLayout excels in scenarios involving:
* **Complex Relationships:** When the position of a composable depends on multiple other composables in ways that are difficult or inefficient to express with simple nesting of Rows and Columns (e.g., centering an element between two others, aligning baselines).
* **Flat Hierarchies**: It allows you to create complex UIs with a flatter structure compared to deep nesting of basic layouts. Flatter hierarchies can sometimes lead to better performance and easier layout inspection.
* **Relative Positioning:** Defining positions relative to siblings or the parent boundaries using constraints (start, end, top, bottom, baseline, guidelines, barriers) is its core strength.
* **Adaptability:** It can be very useful for creating layouts that adapt well to different screen sizes and orientations, as the relationships between elements are explicitly defined.

In essence, `ConstraintLayout` provides a more declarative way to position elements based on relationships rather than just linear stacking. You don't always need it – `simple layouts are often clearer and sufficient with Row and Column` – **but it's a valuable tool for more intricate UI designs.**

