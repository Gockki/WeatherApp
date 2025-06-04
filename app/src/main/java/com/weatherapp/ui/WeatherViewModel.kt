package com.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.data.model.OneCallResponse
import com.weatherapp.data.repository.WeatherRepository
import com.weatherapp.data.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.weatherapp.utils.ApiKeys

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    // OpenWeatherMap API key
    private val apiKey = ApiKeys.OPENWEATHER_API_KEY

    // Helsinki coordinates (fallback)
    private val defaultLat = 60.1699
    private val defaultLon = 24.9384

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState

    init {
        viewModelScope.launch {
            fetchWeatherWithLocation()
        }
    }

    suspend fun fetchWeatherWithLocation() {
        locationManager.getUserLocation().fold(
            onSuccess = { location ->
                fetchWeather(location.latitude, location.longitude)
            },
            onFailure = {
                // Fallback to default location
                fetchWeather(defaultLat, defaultLon)
            }
        )
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            repository.getWeatherData(lat, lon, apiKey)
                .catch { e ->
                    _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { data ->
                            _weatherState.value = WeatherState.Success(data)
                        },
                        onFailure = { e ->
                            _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
                        }
                    )
                }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            fetchWeatherWithLocation()
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: OneCallResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}