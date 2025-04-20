# Jetpack Compose Navigation Summary

Jetpack Compose Navigation provides a declarative way to handle navigation between different composable screens within your Compose application. It integrates directly into your Compose UI code.

Jetpack Compose Navigation provides a framework for navigating between different screens while maintaining `a single-activity architecture`

**Maintaining a Single-Activity Architecture:** This is the key architectural pattern that Compose Navigation strongly facilitates and encourages.

-   Instead of launching a new `Activity` for each screen (the older Android pattern), you typically have one main `Activity` that hosts your Compose UI.
-   Inside this single `Activity`, the `NavHost` composable manages swapping different screen composables in and out based on navigation actions.
-   This avoids the overhead and complexities associated with managing multiple Activity lifecycles, intents for data passing between activities, and task stacks.
-   It aligns well with modern Android development practices, simplifying state management, transitions, and handling configuration changes.


## Core Components

### `NavHostController`

* The central API for managing app navigation.
* Keeps track of the back stack of composable destinations.
* Created using `rememberNavController()`.
* Used to trigger navigation actions (`Maps`, `popBackStack`).

### `NavHost`

* A composable that acts as a container for your navigation graph.
* Displays the current destination based on the `NavHostController`'s state.
* Requires the `navController` and a `startDestination` (the route string of the initial screen).
* Defines the navigation graph by containing `composable` destinations.

### `composable` Function (Key Element)

* Used *inside* the `NavHost`'s lambda body to define each individual screen or destination in the navigation graph.
* Takes a `route`: A unique `String` identifying the destination. This string can include argument placeholders like `/{argName}` or `?argName={value}`.
* Can optionally take an `arguments` parameter: A list of `navArgument` definitions specifying the name, type (`NavType`), nullability, and default value for arguments expected by this route.
* **Crucially, the lambda content `{ ... }` provided to the `composable` function *is the actual UI* for that specific route.** Whatever composable functions you call inside this lambda (e.g., `WeatherDetailScreen(...)`, `UserProfileScreen(...)`) are what will be rendered on the screen when the application navigates *to this specific route*.
* The lambda receives a `NavBackStackEntry` as a parameter, which allows you to access the arguments passed to this destination.

```kotlin
// Example within NavHost { ... }
composable(
    // Route definition with a placeholder for an argument
    route = "weatherDetail/{cityIndex}",
    // Explicitly defining the argument's type
    arguments = listOf(navArgument("cityIndex") { type = NavType.IntType })
) { backStackEntry -> // Lambda providing the UI

    // Retrieve arguments passed via the route
    val cityIndex = backStackEntry.arguments?.getInt("cityIndex") ?: 0
    val weatherState by viewModel.weatherState.collectAsState() // Example state access

    // *** This is where you define or call the UI for the 'weatherDetail' route ***
    // When navController.navigate("weatherDetail/5") is called,
    // this WeatherDetailScreen composable will be displayed.
    if (weatherState is WeatherDataState.Success) {
        val data = (weatherState as WeatherDataState.Success).data
        if (cityIndex < data.size) {
            WeatherDetailScreen(data[cityIndex], navController, cityIndex)
        }
    }
    // Or you could define simpler UI directly: Text("Detail Screen for Index: $cityIndex")
}
```


## Navigation Arguments:

-   Often, you need to pass data from one screen to another (e.g., which item was clicked).
-   **Defining Arguments:** You define arguments within the `composable` function using the `arguments` parameter, which takes a list of `navArgument`.
    -   `navArgument("argumentName") { type = NavType.IntType }`: Specifies the name and type of the argument. Supported types include `StringType`, `IntType`, `FloatType`, `BoolType`, `LongType`, and custom parcelable/serializable types. You can also specify `defaultValue` and `nullable`.
    -   **In your code:** `arguments = listOf(navArgument("cityIndex") { type = NavType.IntType })`. This tells the navigation library to expect an integer argument named `cityIndex` for the `weatherDetail` and `imagePreview` routes.
-   **Passing Arguments:** When navigating, you include the argument value directly in the route string.
    -   **In your code:** `navController.navigate("weatherDetail/$index")` inside `buildWeatherItem`. Here, the actual integer value of `index` replaces the `{cityIndex}` placeholder.
-   **Retrieving Arguments:** Inside the destination's `composable` lambda, you get access to a `NavBackStackEntry`. Its `arguments` bundle contains the passed values.
    -   **In your code:** `val cityIndex = backStackEntry.arguments?.getInt("cityIndex") ?: 0`. This retrieves the integer value associated with the key `"cityIndex"` from the arguments passed via the route. The `?: 0` provides a default fallback.

## Destination with Multiple Arguments

Let's say you want a user profile screen that requires a `userId` (String) and optionally shows a detailed bio (`showBio` - Boolean, defaults to false).

```kotlin
// 1. Define the composable in your NavHost
NavHost(navController = navController, startDestination = "home") {
    composable("home") { HomeScreen(navController) } // Example home screen

    composable(
        route = "profile/{userId}?showBio={showBio}", // Use query param syntax for optional args
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType }, // Required argument
            navArgument("showBio") { // Optional argument
                type = NavType.BoolType
                defaultValue = false // Default value if not provided
            }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") // Retrieve String
        // Retrieve Boolean, using the default value if it's null or not passed
        val showBio = backStackEntry.arguments?.getBoolean("showBio") ?: false 

        // Check userId is not null before proceeding in a real app
        if (userId != null) {
            UserProfileScreen(userId = userId, showDetailedBio = showBio, navController = navController)
        } else {
            // Handle error: userId was unexpectedly null
            Text("Error: User ID missing")
        }
    }
    // Other destinations...
}

// 2. Composable for the User Profile Screen
@Composable
fun UserProfileScreen(userId: String, showDetailedBio: Boolean, navController: NavHostController) {
    Column {
        Text("Profile for User ID: $userId")
        if (showDetailedBio) {
            Text("Showing detailed biography...")
            // Add more detailed info here
        } else {
            Text("Showing summary biography.")
        }
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

// 3. How to navigate to it:

// Example navigation from HomeScreen
@Composable
fun HomeScreen(navController: NavHostController) {
    Column {
        // Navigate with only the required argument (showBio will be false)
        Button(onClick = { navController.navigate("profile/user123") }) {
            Text("View Profile user123 (Summary)")
        }

        // Navigate with both arguments (showBio will be true)
        Button(onClick = { navController.navigate("profile/user456?showBio=true") }) {
            Text("View Profile user456 (Detailed)")
        }
        
        // Navigate with only required argument (showBio will be default false)
        // Alternative way using query syntax for required arg too (works but less common)
        Button(onClick = { navController.navigate("profile/user789?showBio=false") }) {
             Text("View Profile user789 (Summary Explicit)")
        }
    }
}
```