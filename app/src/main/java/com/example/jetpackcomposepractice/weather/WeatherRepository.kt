package com.example.jetpackcomposepractice.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {

    companion object {
        fun getWeatherService() : WeatherService {
            return Retrofit
                .Builder()
                .baseUrl("https://raw.githubusercontent.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
        }
    }

    suspend fun getWeatherData() : List<WeatherData> {
        return getWeatherService()
            .getWeatherData()
    }
}

// Update the WeatherData class to include all fields from JSON
data class WeatherData(
    val cityName: String,
    val state: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Int,
    val weatherCondition: String,
    val description: String,
    val famousFor: List<String>,
    val bestTimeToVisit: String,
    val airportCode: String,
    val imageUrl: String,
    val backgroundColor: String
)