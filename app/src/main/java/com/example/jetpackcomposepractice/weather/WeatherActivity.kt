package com.example.jetpackcomposepractice.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackcomposepractice.ui.theme.JetpackComposePracticeTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBarsPadding

class WeatherActivity : ComponentActivity() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            JetpackComposePracticeTheme {
                WeatherScreen(viewModel)
            }
        }
        
        // Fetch weather data when activity is created
        viewModel.getWeatherData()
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
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
                    buildWeatherList(data)
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
fun buildWeatherItem(data: WeatherData) {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "City Name: ${data.cityName}")
        Text(text = "Description: ${data.description}")
        Row {
            Text(text = "Temperature: ${data.temperature}")
            Text(text = "Wind Speed: ${data.windSpeed}", modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Composable
fun buildWeatherList(dataList: List<WeatherData>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(dataList) { item ->
            buildWeatherItem(item)
        }
    }
}