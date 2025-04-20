package com.example.jetpackcomposepractice.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposepractice.R
import com.example.jetpackcomposepractice.ui.theme.JetpackComposePracticeTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.text.style.TextOverflow

class WeatherActivity : ComponentActivity() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            JetpackComposePracticeTheme {
                WeatherNavHost(viewModel)
            }
        }
        
        // Fetch weather data when activity is created
        viewModel.getWeatherData()
    }
}

@Composable
fun WeatherNavHost(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    
    // In the NavHost composable, change the imagePreview route
    // In the NavHost composable, update the WeatherDetailScreen call to include cityIndex
    NavHost(navController = navController, startDestination = "weatherList") {
        composable("weatherList") {
            WeatherScreen(viewModel, navController)
        }
        composable(
            route = "weatherDetail/{cityIndex}",
            arguments = listOf(navArgument("cityIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val cityIndex = backStackEntry.arguments?.getInt("cityIndex") ?: 0
            val weatherState by viewModel.weatherState.collectAsState()
            
            if (weatherState is WeatherDataState.Success) {
                val data = (weatherState as WeatherDataState.Success).data
                if (cityIndex < data.size) {
                    WeatherDetailScreen(data[cityIndex], navController, cityIndex)
                }
            }
        }
        // Update the imagePreview route in the NavHost
        composable(
            route = "imagePreview/{cityIndex}",
            arguments = listOf(navArgument("cityIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val cityIndex = backStackEntry.arguments?.getInt("cityIndex") ?: 0
            val weatherState by viewModel.weatherState.collectAsState()
            
            if (weatherState is WeatherDataState.Success) {
                val data = (weatherState as WeatherDataState.Success).data
                if (cityIndex < data.size) {
                    ImagePreviewScreen(data[cityIndex], navController)
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, navController: NavHostController) {
    // Collect the state flow as a state in Compose
    val weatherState by viewModel.weatherState.collectAsState()
    // The code has been refactored to use Jetpack Compose's reactive approach with collectAsState() ,
    // which automatically handles the lifecycle concerns for you. When the composable is active,
    // it will collect from the flow, and when it's not active (like when the app is in the background),
    // collection is automatically paused.
    //The previous implementation was using the imperative approach with lifecycleScope.launch and
    // manually handling the lifecycle with repeatOnLifecycle , but now that you're using Compose's
    // declarative approach with state collection, that's no longer necessary.
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(), // This will add padding for system bars so that Refresh button won't overlap on status bar
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weather Data",
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(
                    onClick = { viewModel.getWeatherData() }
                ) {
                    Text("Refresh")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (weatherState) {
                is WeatherDataState.Loading -> {
                    // Show loading UI
                    LoadingView()
                }
                is WeatherDataState.Error -> {
                    // Show error UI with retry button
                    ErrorView { viewModel.getWeatherData() }
                }
                is WeatherDataState.Success -> {
                    // Show success UI with data
                    val data = (weatherState as WeatherDataState.Success).data
                    buildWeatherList(data, navController)
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Loading weather data...")
    }
}

@Composable
fun ErrorView(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Failed to load weather data",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun buildWeatherItem(data: WeatherData, navController: NavHostController, index: Int) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(data.backgroundColor))
    } catch (e: Exception) {
        Color.LightGray
    }
    
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { navController.navigate("weatherDetail/$index") }
            .padding(16.dp)
    ) {
        Text(
            text = "${data.cityName}, ${data.state}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Weather: ${data.weatherCondition}")
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = data.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(text = "Temperature: ${data.temperature}°C")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Wind: ${data.windSpeed} km/h")
        }
    }
}

@Composable
fun buildWeatherList(dataList: List<WeatherData>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
    ) {
        items(dataList.indices.toList()) { index ->
            buildWeatherItem(dataList[index], navController, index)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(data: WeatherData, navController: NavHostController, cityIndex: Int) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(data.backgroundColor))
    } catch (e: Exception) {
        Color.LightGray
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "${data.cityName} (${data.airportCode})",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor.copy(alpha = 0.3f)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Image
            // In the WeatherDetailScreen function, update the image clickable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate("imagePreview/$cityIndex")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (data.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = data.cityName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.no_image,
                        contentDescription = "No Image Available",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weather condition
            Text(
                text = "Weather Condition",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = data.weatherCondition)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weather details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Temperature",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${data.temperature}°C")
                }
                
                Column {
                    Text(
                        text = "Wind Speed",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${data.windSpeed} km/h")
                }
                
                Column {
                    Text(
                        text = "Humidity",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${data.humidity}%")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = data.description)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Famous for
            Text(
                text = "Famous For",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            data.famousFor.forEach { attraction ->
                Text(text = "• $attraction")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Best time to visit
            Text(
                text = "Best Time to Visit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = data.bestTimeToVisit)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(data: WeatherData, navController: NavHostController) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(data.backgroundColor))
    } catch (e: Exception) {
        Color.LightGray
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Image Preview: ${data.cityName}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (data.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = data.cityName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.no_image,
                        contentDescription = "No Image Available",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Back to Details")
                }
                
                Button(
                    onClick = { 
                        navController.popBackStack("weatherList", false)
                    }
                ) {
                    Text("Back to List")
                }
            }
        }
    }
}