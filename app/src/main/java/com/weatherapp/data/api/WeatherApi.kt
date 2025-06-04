package com.weatherapp.data.api

import com.weatherapp.data.model.OneCallResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/3.0/onecall")
    suspend fun getOneCallWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): OneCallResponse
    
    @GET("data/3.0/onecall/timemachine")
    suspend fun getHistoricalWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("dt") timestamp: Long,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): OneCallResponse
    
    @GET("data/3.0/onecall/overview")
    suspend fun getWeatherOverview(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): OneCallResponse
}