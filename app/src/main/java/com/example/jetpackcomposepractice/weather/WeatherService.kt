package com.example.jetpackcomposepractice.weather

import retrofit2.http.GET

interface WeatherService {

    @GET("ramakrishnajoshi/JetpackComposePractice/refs/heads/master/test_jsons/weather_list.json")
    suspend fun getWeatherData(): List<WeatherData>

}