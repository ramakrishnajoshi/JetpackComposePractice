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

data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val windSpeed: Double,
    val description: String
)