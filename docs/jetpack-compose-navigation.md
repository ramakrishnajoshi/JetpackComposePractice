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