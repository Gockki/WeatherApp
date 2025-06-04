package com.weatherapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.data.model.DailyForecast
import com.weatherapp.data.model.OneCallResponse
import com.weatherapp.ui.WeatherState
import com.weatherapp.ui.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = weatherState) {
            is WeatherState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is WeatherState.Error -> {
                ErrorView(message = state.message, onRetry = {
                    viewModel.fetchWeather(60.1699, 24.9384) // Helsinki coordinates
                })
            }
            is WeatherState.Success -> {
                WeatherContent(state.data)
            }
        }
    }
}

@Composable
fun WeatherContent(data: OneCallResponse) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Sijainnin nimi on timezone-kentässä One Call API:ssa
        Text(
            text = data.timezone.substringAfterLast("/").replace("_", " "),
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            text = data.current.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Lämpötila
        Text(
            text = "${data.current.temp.roundToInt()}°C",
            fontSize = 72.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Säätiedot
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                WeatherDetailRow("Tuntuu kuin", "${data.current.feels_like.roundToInt()}°C")
                WeatherDetailRow("Kosteus", "${data.current.humidity}%")
                WeatherDetailRow("Tuuli", "${data.current.wind_speed} m/s")
                WeatherDetailRow("Näkyvyys", "${data.current.visibility / 1000} km")
                WeatherDetailRow("UV-indeksi", "${data.current.uvi}")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Päiväennuste, jos saatavilla
        if (!data.daily.isNullOrEmpty()) {
            Text(
                text = "7 päivän ennuste",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(data.daily) { dailyForecast ->
                    DailyForecastItem(dailyForecast)
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: DailyForecast) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(100.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = SimpleDateFormat("EEE", Locale.getDefault())
                    .format(Date(forecast.dt * 1000)),
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Sääikoni voitaisiin lisätä tähän
            
            Text(
                text = "${forecast.temp.max.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "${forecast.temp.min.roundToInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Virhe: $message",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRetry) {
            Text(text = "Yritä uudelleen")
        }
    }
}