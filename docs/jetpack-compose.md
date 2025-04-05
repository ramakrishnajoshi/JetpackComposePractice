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