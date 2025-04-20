package com.example.jetpackcomposepractice.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {

    val repo = WeatherRepository()

    private var weatherDataState = MutableStateFlow<WeatherDataState>(WeatherDataState.Loading)
    val weatherState = weatherDataState

    fun getWeatherData() {
        weatherState.value = WeatherDataState.Loading
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val result = repo.getWeatherData()
                weatherState.emit(WeatherDataState.Success(result))
            } catch (e: Exception) {
                weatherState.emit(WeatherDataState.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }
}

sealed class WeatherDataState {
    object Loading: WeatherDataState()
    data class Success(val data: List<WeatherData>): WeatherDataState()
    data class Error(val message: String): WeatherDataState()
}