package com.weatherapp.data.repository

import com.weatherapp.data.api.WeatherApi
import com.weatherapp.data.model.OneCallResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeatherData(lat: Double, lon: Double, apiKey: String): Flow<Result<OneCallResponse>> = flow {
        try {
            val weatherData = weatherApi.getOneCallWeather(lat, lon, apiKey = apiKey)
            emit(Result.success(weatherData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun getWeatherOverview(lat: Double, lon: Double, apiKey: String): Flow<Result<OneCallResponse>> = flow {
        try {
            val overviewData = weatherApi.getWeatherOverview(lat, lon, apiKey = apiKey)
            emit(Result.success(overviewData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}